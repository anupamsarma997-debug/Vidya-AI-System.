#!/usr/bin/env python3
"""Train and evaluate a MobileNetV3-Small baseline on an ImageFolder dataset.

Expected layout: root/{train,val,test}/{class_name}/*.(jpg|jpeg|png|bmp|webp)
This is a generic image-classification baseline, not a machine-repair model.
"""
from __future__ import annotations

import argparse
import csv
import json
import random
from pathlib import Path


def parse_args():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--data-root", type=Path, required=True)
    parser.add_argument("--output-dir", type=Path, required=True)
    parser.add_argument("--epochs", type=int, default=10)
    parser.add_argument("--batch-size", type=int, default=32)
    parser.add_argument("--image-size", type=int, default=224)
    parser.add_argument("--workers", type=int, default=2)
    parser.add_argument("--seed", type=int, default=42)
    parser.add_argument("--learning-rate", type=float, default=0.0003)
    return parser.parse_args()


def main():
    args = parse_args()
    if args.epochs < 1 or args.batch_size < 1 or args.image_size < 32:
        raise SystemExit("epochs/batch-size must be positive and image-size >= 32")

    try:
        import torch
        from torch import nn
        from torch.utils.data import DataLoader
        from torchvision import datasets, transforms
        from torchvision.models import MobileNet_V3_Small_Weights, mobilenet_v3_small
    except ImportError as exc:
        raise SystemExit("Install dependencies first: python -m pip install torch torchvision pillow") from exc

    random.seed(args.seed)
    torch.manual_seed(args.seed)
    if torch.cuda.is_available():
        torch.cuda.manual_seed_all(args.seed)

    root = args.data_root
    for split in ("train", "val", "test"):
        if not (root / split).is_dir():
            raise SystemExit(f"Missing required directory: {root / split}")

    weights = MobileNet_V3_Small_Weights.DEFAULT
    mean, std = weights.transforms().mean, weights.transforms().std
    train_tf = transforms.Compose([
        transforms.Resize((args.image_size, args.image_size)),
        transforms.RandomHorizontalFlip(),
        transforms.RandomRotation(8),
        transforms.ToTensor(),
        transforms.Normalize(mean, std),
    ])
    eval_tf = transforms.Compose([
        transforms.Resize((args.image_size, args.image_size)),
        transforms.ToTensor(),
        transforms.Normalize(mean, std),
    ])

    train_ds = datasets.ImageFolder(root / "train", transform=train_tf)
    val_ds = datasets.ImageFolder(root / "val", transform=eval_tf)
    test_ds = datasets.ImageFolder(root / "test", transform=eval_tf)
    if not train_ds.classes or train_ds.classes != val_ds.classes or train_ds.classes != test_ds.classes:
        raise SystemExit("train/val/test must contain the same non-empty class folders")
    if len(train_ds) == 0 or len(val_ds) == 0 or len(test_ds) == 0:
        raise SystemExit("train, val, and test splits must all contain images")

    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    loader_args = {"batch_size": args.batch_size, "num_workers": args.workers}
    train_loader = DataLoader(train_ds, shuffle=True, **loader_args)
    val_loader = DataLoader(val_ds, shuffle=False, **loader_args)
    test_loader = DataLoader(test_ds, shuffle=False, **loader_args)

    model = mobilenet_v3_small(weights=weights)
    model.classifier[-1] = nn.Linear(model.classifier[-1].in_features, len(train_ds.classes))
    model.to(device)
    optimizer = torch.optim.AdamW(model.parameters(), lr=args.learning_rate)
    criterion = nn.CrossEntropyLoss()

    def evaluate(loader):
        model.eval()
        loss_sum = 0.0
        correct = 0
        total = 0
        with torch.no_grad():
            for images, labels in loader:
                images, labels = images.to(device), labels.to(device)
                logits = model(images)
                loss_sum += criterion(logits, labels).item() * labels.size(0)
                correct += (logits.argmax(1) == labels).sum().item()
                total += labels.size(0)
        return {"loss": loss_sum / total, "accuracy": correct / total, "samples": total}

    args.output_dir.mkdir(parents=True, exist_ok=True)
    best_val = float("inf")
    history = []
    for epoch in range(1, args.epochs + 1):
        model.train()
        train_loss_sum = 0.0
        train_total = 0
        for images, labels in train_loader:
            images, labels = images.to(device), labels.to(device)
            optimizer.zero_grad(set_to_none=True)
            loss = criterion(model(images), labels)
            loss.backward()
            optimizer.step()
            train_loss_sum += loss.item() * labels.size(0)
            train_total += labels.size(0)
        val_metrics = evaluate(val_loader)
        row = {
            "epoch": epoch,
            "train_loss": train_loss_sum / train_total,
            "val_loss": val_metrics["loss"],
            "val_accuracy": val_metrics["accuracy"],
        }
        history.append(row)
        print(json.dumps(row), flush=True)
        if val_metrics["loss"] < best_val:
            best_val = val_metrics["loss"]
            torch.save({
                "state_dict": model.state_dict(),
                "classes": train_ds.classes,
                "image_size": args.image_size,
                "architecture": "mobilenet_v3_small",
                "seed": args.seed,
            }, args.output_dir / "best_model.pth")

    checkpoint = torch.load(args.output_dir / "best_model.pth", map_location=device)
    model.load_state_dict(checkpoint["state_dict"])
    test_metrics = evaluate(test_loader)
    with (args.output_dir / "training_history.csv").open("w", newline="", encoding="utf-8") as stream:
        writer = csv.DictWriter(stream, fieldnames=["epoch", "train_loss", "val_loss", "val_accuracy"])
        writer.writeheader()
        writer.writerows(history)
    (args.output_dir / "labels.json").write_text(
        json.dumps({"classes": train_ds.classes}, indent=2), encoding="utf-8"
    )
    report = {
        "architecture": "mobilenet_v3_small",
        "dataset_root": str(root.resolve()),
        "class_to_idx": train_ds.class_to_idx,
        "split_counts": {"train": len(train_ds), "val": len(val_ds), "test": len(test_ds)},
        "test_metrics": test_metrics,
        "device": str(device),
        "seed": args.seed,
        "warning": "Metrics apply only to this dataset and split; not evidence of machine-part fitment or repair safety.",
    }
    (args.output_dir / "test_metrics.json").write_text(json.dumps(report, indent=2), encoding="utf-8")
    print(json.dumps(report, indent=2))


if __name__ == "__main__":
    main()
