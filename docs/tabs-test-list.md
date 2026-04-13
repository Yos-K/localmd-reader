# Tabs Test List

## Domain Rules

- [x] initial tabs expose the initial document as the active tab
- [x] opening a new document adds a new active tab
- [x] opening an existing URI replaces that tab and activates it without duplicate tabs
- [x] activating a tab index switches the active document
- [x] closing an inactive tab keeps the current active document
- [x] closing the active middle tab activates the next document
- [x] closing the active last tab activates the previous document
- [x] closing the only tab keeps the last document open

## Android Behavior

- [x] show a horizontal tab row above the Markdown viewer
- [x] render each open document as a tab button
- [x] disable the active tab button so the current tab is distinguishable
- [x] switch the rendered document when a tab is selected
- [x] open files from the picker, recent list, or external view intent as tabs
- [x] keep the tab row swipe gesture dedicated to horizontal tab scrolling
- [x] close an open tab from the tab row
- [ ] restore tabs after app restart
