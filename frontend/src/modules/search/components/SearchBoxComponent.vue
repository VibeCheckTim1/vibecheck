<script setup lang="ts">
import { computed, ref, watch } from "vue";
import InputText from "../../../components/InputText.vue";
import { useFormField } from "../../../composables/useFormField.ts";
import { ApiError, useHttpClient } from "../../../composables/useHttpClient.ts";
import { useRouter } from "vue-router";
import { useSpotifyService } from "../../search/composables/useSpotifyService.ts"
import { useToast } from "../../../composables/useToast.ts";

type SearchItem = {
    id: string;
    type: string;
    titleText: string;
    subtitleText: string;
    imageUrl?: string;
    liked?: boolean;
};

const { httpGet } = useHttpClient();
const router = useRouter();
const searchKeyword = useFormField<string>(null);
const results = ref<SearchItem[]>([]);
const loading = ref(false);
const { showSuccess, showError } = useToast();


const handleClick = (item: SearchItem) => {
    switch (item.type) {
        case "user":
            router.push({
                name: "account",
                params: {
                    userId: item.id,
                }
            });
            break;

        case "song":
            const youtubeUrl = buildYoutubeSearchUrl(item);
            window.open(youtubeUrl, "_blank");
            break;



        default:
            console.warn("Unknown type:", item.type);
    }
};

const getIcon = (type: string) => {
    switch (type) {
        case "song":
            return "icon-music-note";
        case "artist":
            return "icon-microphone";
        case "playlist":
            return "icon-playlist-music";
        case "album":
            return "icon-album";
        case "user":
            return "icon-account";
        default:
            return "icon-magnify";
    }
};

let timeout: any = null;
const search = async (query: string | null) => {
    if (!query || query.length < 2) {
        results.value = [];
        return;
    }

    loading.value = true;

    try {
        results.value = await httpGet<SearchItem[]>(`/search?q=${encodeURIComponent(query)}`);
    }
    catch (e) {
        console.error("Search error", e);
        results.value = [];
    }
    finally {
        loading.value = false;
    }
};



watch(() => searchKeyword.getInputValue(), (val) => {
    clearTimeout(timeout);
    timeout = setTimeout(() => {
        search(val);
    }, 300);
});

const groupedResults = computed(() => {
    const groups: Record<string, SearchItem[]> = {};

    for (const item of results.value) {
        if (!groups[item.type]) {
            groups[item.type] = [];
        }
        groups[item.type].push(item);
    }

    return groups;
});

const typeLabels: Record<string, string> = {
    user: "Users",
    artist: "Artists",
    song: "Songs",
    album: "Albums",
    playlist: "Playlists"
};

const buildYoutubeSearchUrl = (item: SearchItem) => {
    const query = `${item.subtitleText} ${item.titleText}`;
    return `https://www.youtube.com/results?search_query=${encodeURIComponent(query)}`;
}


async function songLike(item: SearchItem) {
    try {
        await useSpotifyService().songLike(item.id);
        item.liked = true;
        showSuccess("Updated successfully!");
    }
    catch (error) {
        if (error instanceof ApiError) {
            showError(error.message);
        }
    }
}



</script>

<template>
    <div class="search-box">
        <div class="input-wrapper">
            <InputText :control="searchKeyword" placeholder="Search...">
                <template #prefix>
                    <i class="icon-magnify"></i>
                </template>
            </InputText>

            <div v-if="results.length" class="search-dropdown">
                <div v-for="(items, type) in groupedResults" :key="type" class="result-group">
                    <div class="group-title">{{ typeLabels[type] ?? type }}</div>
                    <div v-for="item in items" :key="item.id" class="result-item" @click="handleClick(item)">
                        <div class="icon">
                            <img v-if="item.imageUrl" :src="item.imageUrl" alt="" class="result-image" />
                            <i v-else :class="getIcon(type)"></i>
                        </div>

                        <div class="text">
                            <div class="title">{{ item.titleText }}</div>
                            <div class="subtitle">{{ item.subtitleText }}</div>
                        </div>

                        <button v-if="item.type === 'song'" class="like-button" @click.stop="songLike(item)">
                            <svg class="heart-icon" :class="{ liked: item.liked }" viewBox="0 0 24 24"
                                aria-hidden="true">
                                <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5
            2 5.42 4.42 3 7.5 3
            c1.74 0 3.41.81 4.5 2.09
            C13.09 3.81 14.76 3 16.5 3
            19.58 3 22 5.42 22 8.5
            c0 3.78-3.4 6.86-8.55 11.54L12 21.35z" />
                            </svg>
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
.like-button {
    margin-left: auto;
    min-width: 42px;
    width: 42px;
    height: 42px;
    border: none;
    border-radius: 50%;
    background: transparent;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
}

.like-button:hover {
    background: rgba(0, 0, 0, 0.08);
}

.heart-icon {
    width: 28px;
    height: 28px;
    fill: transparent;
    stroke: #e91e63;
    stroke-width: 2;
}

.heart-icon.liked {
    fill: #e91e63;
}


.result-image {
    width: 40px;
    height: 40px;
    border-radius: 8px;
    object-fit: cover;
}

.search-box {
    width: 100%;

    .input-wrapper {
        position: relative;
        width: 100%;
    }

    .search-dropdown {
        position: absolute;
        top: calc(100% + 8px);
        left: 0;
        width: 100%;
        background: white;
        border-radius: var(--border-radius-5);
        box-shadow: 0 0 var(--spacing-4) var(--color-gray-2);
        padding: var(--spacing-3);
        display: flex;
        flex-direction: column;
        gap: var(--spacing-2);
    }

    .result-group {
        display: flex;
        flex-direction: column;

        .group-title {
            font-size: var(--font-size-1);
            font-weight: bold;
            color: var(--color-gray-5);
            margin-bottom: var(--spacing-2);
        }
    }

    .result-item {
        display: flex;
        align-items: center;
        gap: var(--spacing-3);
        padding: var(--spacing-2);
        border-radius: var(--border-radius-5);
        cursor: pointer;

        &:hover {
            background: var(--color-gray-0);
        }

        .icon {
            width: 40px;
            height: 40px;
            background: var(--color-primary-4);
            color: white;
            display: flex;
            align-items: center;
            justify-content: center;
            border-radius: var(--border-radius-4);
            font-size: var(--font-size-2);
        }

        .text {
            display: flex;
            flex-direction: column;
        }

        .title {
            font-weight: 600;
            line-height: 1.2;
        }

        .subtitle {
            font-size: var(--font-size-1);
            color: var(--color-gray-5);
        }
    }
}
</style>
