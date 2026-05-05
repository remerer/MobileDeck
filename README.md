# MobileDeck

MobileDeck turns an Android phone into a Bluetooth HID macro deck for a Windows PC. It sends keyboard hotkeys, media keys, text, and run-command fallback input without requiring a companion app on the PC.

![MobileDeck screenshot](docs/screenshot.png)

## Features

- Bluetooth HID keyboard registration and PC pairing
- Media keys such as mute, volume, play/pause, stop, previous, and next
- Custom hotkeys, text input, and `Win+R` run-command fallback
- Editable deck keys with title, subtitle, icon text, image icon, color, payload, and action type
- Multiple pages, limited to 5 pages
- Page indicator dots
- Optional 2/3-finger page swipe for page switching
- Horizontal or vertical page direction
- Optional looped page swiping at the first and last page
- Layout editor for rows, columns, key editing, and drag movement
- Display modes for icon, icon + keyword, or keyword-only keys
- Icon picker and media-key target picker in key editing

## Install

Download the APK from the GitHub release and install it on the Android device.

Android may require allowing installation from the browser or file manager used to open the APK.

## Pair With Windows

1. Open MobileDeck on Android.
2. Open Settings.
3. Tap `Register HID`.
4. Tap `Make discoverable for pairing`.
5. On Windows, open Bluetooth settings and pair with `MobileDeck Keyboard`.
6. Return to MobileDeck and use the deck keys.

If Windows keeps an old HID descriptor cached after an update, remove the paired MobileDeck device from Windows Bluetooth settings and pair it again.

## Basic Use

- Tap a key on the deck to execute it.
- Open Settings, then `Layout editor`, to edit layout and key placement.
- In Layout editor, tap a key to edit it.
- Drag a key in Layout editor to move it to another slot.
- Long-press an empty slot to create a new key and immediately configure it.
- Use the columns slider plus vertical rows and spacing sliders to resize the grid.
- Delete the current page from Layout editor.
- Reset the first page from Layout editor while keeping the title area and Settings key.
- In key editing, choose `App command` to assign Settings, Bluetooth status, Previous page, or Next page.
- In key editing, choose `Media key` to pick the exact media function from a dropdown.
- Choose whether a key shows only an icon, an icon with keyword text, or keyword text only.

## Page Controls

- Current page is shown with dots at the edge of the deck.
- Page direction can be set to horizontal or vertical in Settings.
- Multi-touch page swipe can be enabled or disabled in Settings.
- Loop page swipe controls whether swiping past the first or last page wraps around.
- `Previous page` and `Next page` are available as assignable key actions.

## Notes

- Text input through Bluetooth HID depends on the current Windows keyboard layout and IME state.
- Reliable exact text input, clipboard paste, direct app launching, and Windows icon extraction are planned as future Windows Companion features.
- Diagnostics are available from Settings.
