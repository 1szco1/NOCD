# NOCD + Hotbar Fix

NOCD stands for **No Opposing Cardinal Directions**. This is a small Fabric client mod that fixes
hotbar key-repeat backlog and adds key cancellation with no restore. It builds for 11 Minecraft
versions: 1.16.5, 1.18.2, 1.19.4, 1.20.1, 1.20.4, 1.21.1, 1.21.4, 1.21.11, 26.1, 26.2 and 26.3.

## What the hotbar fix is

Vanilla counts operating-system key-repeat events as extra key presses. Hold a hotbar number key
and the game queues a slot switch for every repeat event. When you let go, those queued switches
keep firing for a few ticks, so you can land on the wrong slot.

The mod listens to the same key event. On a repeat event (`action == 2`) it sets that key's pending
press count to `0`, so only a fresh physical press can queue a switch. The inventory key gets the
same treatment.

Ticks are untouched. The slot still changes on the next client tick, through the same vanilla code
as before. Nothing is moved earlier, delayed differently, or paced.

## How NOCD works

On a fresh press, the mod works out which group the pressed key belongs to, then releases any held
key that is allowed to be cancelled. A cancelled key is released once and stays released. When you
let go of the new key, the old key does **not** come back. It works again only when you press it
again.

Groups are read from the game's own keybinds, so rebinding works:

- **Movement** (forward, back, left, right): opposite directions cancel (W cancels S, A cancels D).
  Adjacent directions are kept, so W+D still gives a diagonal. Movement never interacts with any
  other group.
- **Hotbar slots**: only another hotbar slot can cancel a hotbar slot. Movement, unbound and action
  keys cannot cancel them.
- **Sneak/sit and every other bound key**: fully immune. They cancel nothing and are cancelled by
  nothing.
- **Unbound keys**: cancel each other.

The hotbar fix and NOCD share the same key event handler, so both are applied in one pass with no
extra tick work.

## How NOCD plays a role in the hotbar

Hotbar slots are their own cancellation group. Pressing another hotbar number releases the held
one, so no leftover press can be queued behind it. Together with the repeat clearing, that means a
hotbar key gives one switch per physical press, and a cancelled slot stays cancelled. The inventory
key is included in the repeat clearing as well, so opening inventory cannot backlog either.

## Why this is better than SOCD

SOCD (Simultaneous Opposing Cardinal Directions) implementations usually restore the previously
held key when the newer key is released. That restore lets a player hold two opposing directions
and get an instant reversal without a fresh press. That switch-back is the part many servers ban.

This mod has no restore step at all. It also is not a whole-keyboard rule: each group is isolated,
action keys are immune, and movement keeps adjacent directions for diagonals. It does not change
tick timing, does not measure or override repeat rate, and does not touch the vanilla switching
path.

## Why it should not be treated as the bannable behaviour

No mod can guarantee what a particular server allows. What can be said precisely is that this mod
does not implement the behaviour those rules target:

- no key is restored when another key is released;
- no ticks are added, removed or reordered;
- no timing or repeat rate is measured or overridden;
- cancellation uses the game's normal key-release API, so the game sees ordinary press and release
  events;
- the only state changed beyond that is the pending press count on a repeat event, set to zero.

## Build

One version: copy `versions/<mc>.properties` to `gradle.properties`, then run Gradle build.
All obfuscated versions: `./build-all.sh` (JDK 21, Gradle 9.2.1).
26.x versions: `./build-26.sh` (JDK 25, Gradle 9.6.0).
Any property set to `UNSET` stops the build with a message. Fill it from https://fabricmc.net/develop
Flavors: `legacy16` = 1.16.5, `mid` = 1.18.2 to 1.21.4, `modern` = 1.21.11, `mojang` = 26.1 to 26.3.
`build26/` is a separate Gradle subproject because Gradle 9.6 dropped the `-c` and `-b` flags.

`test/NocdTest.java` checks the cancellation table and can be compiled on its own against
`src/common/java/dev/nocdhotbarfix/Nocd.java`.

## Credits

- sz_co / 1szco1: this port and its multi-version build
- Disproven: original discoverer of the key-repeat backlog bug and author of the original Hotbar
  Repeat Fix (1.21.11) that this is inspired by
