<script setup lang="ts">
import {computed, ref, watch} from "vue";
import {useRoute, useRouter} from "vue-router";
import PageHeaderComponent from "../../../components/PageHeaderComponent.vue";
import type {Playlist} from "../../../entities/playlist.ts";
import {usePlaylistService} from "../composables/usePlaylistService.ts";
import {ApiError} from "../../../composables/useHttpClient.ts";
import {useToast} from "../../../composables/useToast.ts";
import {useState} from "../../../composables/useState.ts";
import {useDialog} from "../../../composables/useDialog.ts";
import {useConfirm} from "../../../composables/useConfirm.ts";
import EditPlaylistForm from "../components/EditPlaylistForm.vue";

const route = useRoute();
const router = useRouter();
const {showError, showSuccess} = useToast();
const {currentUser} = useState();
const {openDialog} = useDialog();
const {openConfirm} = useConfirm();

const playlist = ref<Playlist | null>(null);
const isLoading = ref(false);

const visibilityLabel = computed(() => {
    if (!playlist.value) return "";
    return playlist.value.isPublic ? "Public" : "Private";
});

const isOwner = computed(() => {
    return !!playlist.value && currentUser.value?.idUser === playlist.value.userId;
});

async function loadPlaylist(playlistId: number) {
    isLoading.value = true;

    try {
        const {getPlaylist} = usePlaylistService();
        playlist.value = await getPlaylist(playlistId);
    }
    catch (error) {
        await router.push({name: "home"});
        if (error instanceof ApiError) {
            showError(error.message);
        }
    }
    finally {
        isLoading.value = false;
    }
}

function editPlaylist() {
    if (!playlist.value) return;

    openDialog(EditPlaylistForm, "Edit playlist", {
        playlist: playlist.value,
        callback: async (updatedPlaylist: Playlist) => {
            playlist.value = updatedPlaylist;
            showSuccess("Playlist updated successfully!");
        }
    });
}

async function deletePlaylist() {
    if (!playlist.value) return;

    const confirmed = await openConfirm({
        isDanger: true,
        title: "Delete playlist",
        subtitle: "Are you sure you want to delete this playlist?",
        acceptMessage: "Yes, delete playlist",
    });

    if (!confirmed || !playlist.value) {
        return;
    }

    try {
        const {deletePlaylist} = usePlaylistService();
        const ownerId = playlist.value.userId;
        await deletePlaylist(playlist.value.id);
        showSuccess("Playlist deleted successfully!");
        await router.push({name: "account", params: {userId: ownerId}});
    }
    catch (error) {
        if (error instanceof ApiError) {
            showError(error.message);
            return;
        }
        showError("Something went wrong.");
    }
}

watch(() => route.params.playlistId, async (newPlaylistId) => {
    if (!newPlaylistId) return;

    await loadPlaylist(Number(newPlaylistId));
}, {
    immediate: true,
});
</script>

<template>
    <PageHeaderComponent>
        <span>Playlist details</span>
        <template #actions v-if="isOwner">
            <button class="secondary-button" type="button" @click="editPlaylist">Edit</button>
            <button class="danger-button" type="button" @click="deletePlaylist">Delete</button>
        </template>
    </PageHeaderComponent>

    <div class="center-content-container">
        <div class="playlist-details-container" v-if="playlist">
            <div class="playlist-cover">
                <i class="icon-playlist-music"></i>
            </div>

            <div class="playlist-heading">
                <span class="playlist-label">Playlist</span>
                <h1>{{ playlist.name }}</h1>
                <router-link class="owner-link" :to="{ name: 'account', params: { userId: playlist.userId } }">
                    @{{ playlist.username }}
                </router-link>
            </div>

            <div class="playlist-info">
                <div class="info-item">
                    <span class="value">{{ playlist.songCount }}</span>
                    <span class="label">Songs</span>
                </div>
                <div class="info-item">
                    <span class="value">{{ visibilityLabel }}</span>
                    <span class="label">Visibility</span>
                </div>
                <div class="info-item">
                    <span class="value">{{ playlist.isFavorite ? "Yes" : "No" }}</span>
                    <span class="label">Favorite</span>
                </div>
            </div>

            <section class="songs-placeholder">
                <div class="section-title">
                    <i class="icon-music"></i>
                    <span>Songs</span>
                </div>
                <div class="empty-state">
                    <p>No songs have been added to this playlist yet.</p>
                </div>
            </section>
        </div>

        <div class="playlist-details-container" v-else-if="isLoading">
            <div class="empty-state">
                <p>Loading playlist...</p>
            </div>
        </div>
    </div>
</template>

<style scoped>
.playlist-details-container {
    padding-block: var(--spacing-5);
    width: 100%;
}

.playlist-cover {
    width: 160px;
    aspect-ratio: 1 / 1;
    background-color: var(--color-gray-0);
    border-radius: var(--border-radius-4);
    display: flex;
    align-items: center;
    justify-content: center;
    color: var(--color-primary-4);
    font-size: var(--font-size-8);
    margin-inline: auto;
    margin-bottom: var(--spacing-4);
}

.playlist-heading {
    text-align: center;
    margin-bottom: var(--spacing-6);

    .playlist-label {
        display: block;
        color: var(--color-gray-5);
        font-size: var(--font-size-1);
        margin-bottom: var(--spacing-1);
    }

    h1 {
        font-size: var(--font-size-6);
        line-height: 1.2;
        color: var(--color-gray-8);
        overflow-wrap: anywhere;
    }

    .owner-link {
        color: var(--color-primary-4);
        font-size: var(--font-size-1);
        font-weight: bold;
        text-decoration: none;

        &:hover {
            text-decoration: underline;
        }
    }
}

.playlist-info {
    width: 100%;
    background-color: var(--color-gray-0);
    border-radius: var(--border-radius-6);
    padding: var(--spacing-2);
    display: grid;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: var(--spacing-2);

    .info-item {
        display: flex;
        align-items: center;
        justify-content: center;
        flex-direction: column;
        text-align: center;
        min-width: 0;
        padding: var(--spacing-2);

        .value {
            font-size: var(--font-size-4);
            font-weight: bold;
            color: var(--color-gray-8);
            overflow-wrap: anywhere;
        }

        .label {
            font-size: var(--font-size-1);
            color: var(--color-gray-5);
        }
    }
}

section {
    margin-top: var(--spacing-8);

    .section-title {
        display: flex;
        align-items: center;
        gap: var(--spacing-2);
        margin-bottom: var(--spacing-2);

        i {
            color: var(--color-primary-4);
            font-size: var(--font-size-5);
        }

        span {
            font-weight: bold;
        }
    }
}
</style>
