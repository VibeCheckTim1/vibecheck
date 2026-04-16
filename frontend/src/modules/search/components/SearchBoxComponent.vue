<script setup lang="ts">
import {computed, ref, watch} from "vue";
import InputText from "../../../components/InputText.vue";
import {useFormField} from "../../../composables/useFormField.ts";
import {useHttpClient} from "../../../composables/useHttpClient.ts";
import {useRouter} from "vue-router";

type SearchItem = {
    id: number;
    type: string;
    titleText: string;
    subtitleText: string;
};

const {httpGet} = useHttpClient();
const router = useRouter();
const searchKeyword = useFormField<string>(null);
const results = ref<SearchItem[]>([]);
const loading = ref(false);

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
</script>

<template>
    <div class="search-box">
        <div class="input-wrapper">
            <InputText :control="searchKeyword"
                       placeholder="Search...">
                <template #prefix>
                    <i class="icon-magnify"></i>
                </template>
            </InputText>

            <div v-if="results.length" class="search-dropdown">
                <div v-for="(items, type) in groupedResults"
                     :key="type"
                     class="result-group">
                    <div class="group-title">{{ typeLabels[type] ?? type }}</div>
                    <div v-for="item in items"
                         :key="item.id"
                         class="result-item"
                         @click="handleClick(item)">
                        <div class="icon">
                            <i :class="getIcon(type)"></i>
                        </div>
                        <div class="text">
                            <div class="title">{{ item.titleText }}</div>
                            <div class="subtitle">{{ item.subtitleText }}</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
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
