// Single source of truth for this app's network destinations: the Alchemy
// API base and the voice Bridge base. Values are compiled in from
// src/config/destinations.json, generated at prestart/prebuild time by
// scripts/sync-destinations.js from the one registry (see
// src/config/destinations.defaults.json). No literal host/port belongs
// anywhere else in this app.
//
// Same selection rule as Lexicon's src/utils/apiUrls.js: local access (by
// hostname sniff) uses the LAN address, alex-dyakin.com uses the public
// Cloudflare URL, and anything else falls back to the PlayIt tunnel. This
// app has no native (Capacitor) shell, so there is no native branch.

import destinations from './config/destinations.json';

const {
    LAN_HOST,
    ALCHEMY_PORT,
    PUBLIC_ALCHEMY_URL,
    PUBLIC_BRIDGE_URL,
    PLAYIT_HOST,
    PLAYIT_ALCHEMY_PORT,
} = destinations;

function resolveApiUrl() {
    const hostname = typeof window !== 'undefined' ? window.location.hostname : '';

    const isLocalAccess = (
        hostname === 'localhost' ||
        hostname === '127.0.0.1' ||
        hostname.startsWith('192.168.') ||
        hostname.startsWith('10.') ||
        hostname.startsWith('172.')
    );
    const isCloudflareAccess = hostname.endsWith('alex-dyakin.com');

    if (isLocalAccess) {
        return `http://${LAN_HOST}:${ALCHEMY_PORT}`;
    }
    if (isCloudflareAccess) {
        return PUBLIC_ALCHEMY_URL;
    }
    return `http://${PLAYIT_HOST}:${PLAYIT_ALCHEMY_PORT}`;
}

export const API_URL = resolveApiUrl();

// Bridge always uses the Cloudflare tunnel (not hosted locally or via PlayIt).
export const BRIDGE_URL = PUBLIC_BRIDGE_URL;
