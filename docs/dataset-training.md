# Real dataset shortlist and baseline training

**Current evidence:** dataset sources have been identified, but no external image archive has been downloaded, extracted, or trained in this repository. Do not report a model as trained until an actual run produces its checkpoint and held-out metrics.

## Highest-priority sources

1. **M2 screw defect detection dataset (5,536 images).** [Mendeley Data, version 2](https://data.mendeley.com/datasets/2y6mcsyrpy/2) lists BODY.rar (2.72 GB) and HEAD.rar (1.02 GB), and declares CC BY 4.0. This is a narrow OK/NG defect-classification dataset for screw body and head, not a general machine-parts identity dataset. Confirm the extracted folder layout and retain attribution/license information.
2. **IRISP spare-parts retrieval (17,410 objects / 141,937 images).** [Project repository](https://github.com/imyliu1717/industrial_spare_part_dataset) declares CC BY-SA 4.0 and describes mobile and machine-image domains. Its README currently says the full dataset download is TBA, so the counts are metadata, not available training images.
3. **Fraunhofer MVIP.** [Fordatis record](https://fordatis.fraunhofer.de/handle/fordatis/358) describes multimodal, multi-view industrial part recognition. Exact license terms and access/storage requirements need confirmation before use.

Other leads and their restrictions are recorded in `data/dataset-register.csv`. In particular, MVTec AD/AD2 are non-commercial-only under CC BY-NC-SA 4.0. The NEU steel-defect dataset is useful for a separate surface-defect task, but its official page does not provide a clear reuse license. Do not silently mix defect labels with part-identity labels.

## Baseline trainer

`tools/train_imagefolder_baseline.py` trains a MobileNetV3-Small classifier using PyTorch and torchvision. It expects:

```text
DATA_ROOT/
  train/class_a/image.jpg
  val/class_a/image.jpg
  test/class_a/image.jpg
```

Every split must contain the same class folders. Keep the publisher's original test split separate. If the archive has distinct screw head/body subsets, train and evaluate them separately rather than mixing incompatible image domains.

After obtaining permission and downloading/extracting the dataset in a cloud notebook or computer with enough disk space:

```bash
python -m pip install torch torchvision pillow
python tools/train_imagefolder_baseline.py --data-root /path/to/prepared-data --output-dir ./vidya-baseline --epochs 10
```

Outputs: `best_model.pth`, `labels.json`, `training_history.csv`, and `test_metrics.json`. The script uses ImageNet-pretrained MobileNetV3-Small weights, trains on the train split, selects the best checkpoint by validation loss, and evaluates the held-out test split once at the end.

These metrics apply only to the exact dataset and split used. They do not prove OEM fitment, identify arbitrary machine components, diagnose mechanical faults, or validate repair instructions. This baseline is not yet integrated into the Android app or converted to an on-device model.

## Why training has not started

The M2 archives are about 3.74 GB in total and are not present in this repository or this execution environment. The larger IRISP dataset is not yet downloadable from its project README. Without actual image files and their labels, running a training command would either fail or train on invented data. The next evidence-backed step is to download the M2 archives in a cloud notebook with sufficient storage, verify the license and archive contents, then prepare the split without contaminating the test set.
