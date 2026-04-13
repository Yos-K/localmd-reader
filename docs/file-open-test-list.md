# File Open Test List

## FileTypeDetector

- [x] accepts `.md` extension
- [x] accepts `.markdown` extension
- [x] accepts uppercase `.MD` extension
- [x] rejects unknown extension
- [x] rejects empty display name
- [x] rejects missing extension

## FileSizePolicy

- [x] accepts zero byte file
- [x] accepts file at maximum size
- [x] rejects file above maximum size
- [x] rejects negative size
- [x] accepts unknown provider size before streaming limit check
