# PRD: Feed Card Hover Interaction

## Goal
- Make feed cards feel actionable at a glance.
- Reinforce hover affordance with subtle motion and visible action cues.

## Scope
- Feed card hover overlay (like/comment/collect icons).
- Hover motion on card and cover image.

## Interaction Rules
- Hover delay: 0-150ms.
- Motion duration: 200-300ms.
- Desktop: overlay fades in on hover.
- Mobile: overlay stays lightly visible (no hover).

## Visual Behavior
- Card lift: translateY(-2px) with stronger shadow.
- Cover zoom: scale to ~1.05.
- Overlay: bottom-right action buttons, white glass chips.
- Gradient veil: subtle dark fade from bottom for contrast.

## Non-Goals
- No real interactions or API calls yet (purely visual).
- No new endpoints.
