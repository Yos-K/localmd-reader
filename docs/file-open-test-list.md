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

## MarkdownFileOpenResult

- [x] returns a readable Markdown file when display name and size are accepted
- [x] returns an unsupported file result when display name is not Markdown
- [x] returns an oversized file result when Markdown file exceeds policy
- [x] prevents readable Markdown file creation with unsupported display name
- [x] prevents readable Markdown file creation with invalid size

## Android Behavior

- [x] show a dialog when the selected file type is unsupported
- [x] show a dialog when the selected file is too large
- [x] show a dialog when the selected document cannot be displayed
- [x] explain unsupported files by supported extensions
- [x] explain oversized files by the configured size limit
- [x] explain unreadable documents by likely access or file state causes
