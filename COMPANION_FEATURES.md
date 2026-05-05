# Companion App Boundary

MobileDeck currently keeps the Windows path execution fallback as Bluetooth HID input:

- send `Win+R`
- type the configured path or command
- send `Enter`

This keeps the Android app usable without installing anything on the PC, but it is not fully reliable when another app captures keyboard input, the desktop is locked, a fullscreen app is active, or Windows focus is not on the expected UI.

## Keep In Android HID App

- Keyboard hotkeys
- Consumer control media keys
- Plain text typing
- Run-command fallback through `Win+R`
- Deck layout, button editing, icon presets, and local persistence
- Android-side user-selected image icons
- Bluetooth HID registration, pairing, and connection diagnostics

## Move To Windows Companion Later

- Directly launch a file, folder, URL, or application path without using `Win+R`
- Type exact text on Windows without depending on the current keyboard layout, IME, or Korean/English input state
- Set Windows clipboard text and paste it into the focused app for reliable long text input
- Read Windows application icons and send icon image metadata to Android
- Enumerate Start Menu entries, pinned apps, installed apps, and recent folders
- Execute shell verbs such as open, run as administrator, reveal in Explorer, or open with a specific app
- Return command success/failure output to Android
- Maintain a stable PC-side connection independent of foreground window focus

## Proposed Companion Contract

The Android app can keep the current `RunCommand` action name and route it as follows:

- Companion connected: send structured command to companion
- Companion unavailable: use the existing HID `Win+R` fallback

Example message:

```json
{
  "type": "open",
  "target": "%USERPROFILE%\\Desktop",
  "mode": "normal"
}
```

Example text message:

```json
{
  "type": "text",
  "value": "Hello 안녕하세요",
  "delivery": "clipboard-paste"
}
```

This keeps the current Deck model compatible while allowing a more reliable PC integration later.
