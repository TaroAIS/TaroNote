# PRD: Feed Loading Skeletons

## Goal
- Replace loading text with skeleton cards.
- Keep masonry layout stable during pagination.

## Scope
- Initial load: show 8-12 skeleton cards.
- Pagination: append 2-4 skeleton cards while fetching.

## Interaction Rules
- Skeletons appear only while network request is in-flight.
- Skeletons are removed when real data arrives.
- No changes to API or data shape.

## Visual Behavior
- Match real card shape: cover (3:4), title lines, avatar line, metadata lines.
- Use subtle pulse to indicate loading.

## Non-Goals
- No shimmer animation.
- No server-side rendering changes.
