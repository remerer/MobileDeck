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
- Optional 2/3-finger page swipe
- Horizontal or vertical page direction
- Layout editor for rows, columns, key editing, and drag movement

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
- Use the columns slider and vertical rows slider to resize the grid.

## Page Controls

- Current page is shown with dots at the edge of the deck.
- Page direction can be set to horizontal or vertical in Settings.
- Multi-touch page swipe can be enabled or disabled in Settings.
- `Previous page` and `Next page` are available as assignable key actions.

## Notes

- Text input through Bluetooth HID depends on the current Windows keyboard layout and IME state.
- Reliable exact text input, clipboard paste, direct app launching, and Windows icon extraction are planned as future Windows Companion features.
- Diagnostics are shown only in debug builds.
