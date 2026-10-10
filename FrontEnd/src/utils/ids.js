// Ids are UUID v7: the first 12 hex digits are the creation time, so ids created close together share
// a prefix. A short, human-readable form must therefore use the random tail, not the head.

/** "0198a3f2-…-8c41a7e2d93b" → "A7E2D93B" */
export const shortId = (id) => String(id || '').replace(/-/g, '').slice(-8).toUpperCase();
