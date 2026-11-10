import importlib.util
import io
import pathlib
import tarfile
import tempfile
import unittest

SCRIPT = pathlib.Path(__file__).parents[1] / "verify_dataset_archive.py"
SPEC = importlib.util.spec_from_file_location("verify_dataset_archive", SCRIPT)
MODULE = importlib.util.module_from_spec(SPEC)
assert SPEC and SPEC.loader
SPEC.loader.exec_module(MODULE)


class ArchiveVerifierTests(unittest.TestCase):
    def make_tar(self, entries):
        temp = tempfile.NamedTemporaryFile(suffix=".tar", delete=False)
        temp.close()
        with tarfile.open(temp.name, "w") as archive:
            for name, kind in entries:
                info = tarfile.TarInfo(name)
                if kind == "file":
                    data = b"vidya-test"
                    info.size = len(data)
                    archive.addfile(info, io.BytesIO(data))
                elif kind == "symlink":
                    info.type = tarfile.SYMTYPE
                    info.linkname = "target"
                    archive.addfile(info)
        return pathlib.Path(temp.name)

    def test_accepts_regular_archive_and_counts_files(self):
        path = self.make_tar([("dataset/image.bin", "file")])
        try:
            self.assertEqual(MODULE.inspect_archive(path), (1, len(b"vidya-test")))
        finally:
            path.unlink()

    def test_rejects_parent_traversal(self):
        path = self.make_tar([("../outside.txt", "file")])
        try:
            with self.assertRaisesRegex(ValueError, "unsafe archive path"):
                MODULE.inspect_archive(path)
        finally:
            path.unlink()

    def test_rejects_symlink_entries(self):
        path = self.make_tar([("dataset/link", "symlink")])
        try:
            with self.assertRaisesRegex(ValueError, "unsupported link/device"):
                MODULE.inspect_archive(path)
        finally:
            path.unlink()


if __name__ == "__main__":
    unittest.main()
