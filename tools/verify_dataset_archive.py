#!/usr/bin/env python3
"""Inspect a downloaded dataset TAR safely without extracting it.

This checks archive readability, member paths, and link/device entries. It does
not validate dataset labels, image quality, provenance, or license compliance.
"""
from __future__ import annotations

import argparse
import hashlib
import pathlib
import sys
import tarfile


def sha256_file(path: pathlib.Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as stream:
        for chunk in iter(lambda: stream.read(1024 * 1024), b""):
            digest.update(chunk)
    return digest.hexdigest()


def safe_member_name(name: str) -> bool:
    candidate = pathlib.PurePosixPath(name)
    return not candidate.is_absolute() and all(part not in ("..", "") for part in candidate.parts)


def inspect_archive(path: pathlib.Path) -> tuple[int, int]:
    files = 0
    total_bytes = 0
    with tarfile.open(path, mode="r:*") as archive:
        for member in archive:
            if not safe_member_name(member.name):
                raise ValueError(f"unsafe archive path: {member.name!r}")
            if member.issym() or member.islnk() or member.isdev() or member.isfifo():
                raise ValueError(f"unsupported link/device entry: {member.name!r}")
            if member.isfile():
                files += 1
                total_bytes += member.size
    return files, total_bytes


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("archive", type=pathlib.Path)
    parser.add_argument("--expected-sha256", help="Optional trusted checksum supplied by the publisher")
    args = parser.parse_args()

    if not args.archive.is_file():
        parser.error(f"archive not found: {args.archive}")
    digest = sha256_file(args.archive)
    if args.expected_sha256 and digest.lower() != args.expected_sha256.lower():
        print("FAIL: SHA-256 does not match expected value", file=sys.stderr)
        return 2

    try:
        files, total_bytes = inspect_archive(args.archive)
    except (tarfile.TarError, OSError, ValueError) as error:
        print(f"FAIL: archive inspection: {error}", file=sys.stderr)
        return 2

    print("PASS: TAR structure readable; unsafe paths and links not found")
    print(f"Archive: {args.archive}")
    print(f"Archive bytes: {args.archive.stat().st_size}")
    print(f"SHA-256: {digest}")
    print(f"Regular files: {files}")
    print(f"Uncompressed regular-file bytes: {total_bytes}")
    print("NOTE: this is an archive-integrity check, not dataset or label validation.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
