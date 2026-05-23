<script setup lang="ts">
import InputText from "../../../components/InputText.vue";
import InputToggle from "../../../components/InputToggle.vue";
import {useDialog} from "../../../composables/useDialog.ts";
import {useForm} from "../../../composables/useForm.ts";
import {useFormField} from "../../../composables/useFormField.ts";
import {useToast} from "../../../composables/useToast.ts";
import {useValidators} from "../../../composables/useValidators.ts";
import {ApiError} from "../../../composables/useHttpClient.ts";
import type {Playlist} from "../../../entities/playlist.ts";
import {usePlaylistService} from "../composables/usePlaylistService.ts";

const props = defineProps<{
    playlist: Playlist;
    callback: (playlist: Playlist) => Promise<void>;
}>();

const {closeDialog} = useDialog();
const {required, maxLength} = useValidators();
const {showError} = useToast();

const form = useForm({
    name: useFormField<string>(props.playlist.name, [required, maxLength(100)]),
    isFavorite: useFormField<boolean>(props.playlist.isFavorite),
    isPublic: useFormField<boolean>(props.playlist.isPublic),
});

async function submitForm() {
    if (!form.validateForm()) {
        return;
    }

    try {
        const {updatePlaylist} = usePlaylistService();
        const updatedPlaylist = await updatePlaylist(props.playlist.id, form.toJson());

        await props.callback(updatedPlaylist);
        closeDialog();
    }
    catch (error) {
        if (error instanceof ApiError) {
            showError(error.message);
            return;
        }
        showError("Something went wrong.");
    }
}
</script>

<template>
    <div class="edit-playlist-container">
        <form @submit.prevent="submitForm">
            <InputText :control="form.name" label="Playlist name"/>

            <div class="form-section-container">
                <div class="icon-holder">
                    <i class="icon-check-circle-outline"></i>
                </div>
                <div class="section-info">
                    <span class="title">Favorite playlist</span>
                    <span class="description">Mark this playlist as one of your favorites</span>
                </div>
                <div class="action-holder">
                    <InputToggle :control="form.isFavorite"/>
                </div>
            </div>

            <div class="form-section-container">
                <div class="icon-holder">
                    <i class="icon-web"></i>
                </div>
                <div class="section-info">
                    <span class="title">Public playlist</span>
                    <span class="description">Other users can see this playlist on your profile</span>
                </div>
                <div class="action-holder">
                    <InputToggle :control="form.isPublic"/>
                </div>
            </div>

            <button type="submit" class="primary-button large-button">Save</button>
        </form>
    </div>
</template>

<style scoped>
.edit-playlist-container {
    padding: var(--spacing-4);
}

.form-section-container {
    width: 100%;
    background-color: var(--color-gray-0);
    border-radius: var(--border-radius-6);
    padding: var(--spacing-3);
    display: grid;
    grid-template-columns: 30px minmax(0, 1fr) auto;
    gap: var(--spacing-2);
    align-items: center;

    .icon-holder {
        width: 30px;
        height: 30px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: var(--font-size-5);
        color: var(--color-primary-4);
    }

    .section-info {
        min-width: 0;
        display: flex;
        flex-direction: column;
        align-items: flex-start;

        .title {
            font-weight: bold;
        }

        .description {
            font-size: var(--font-size-1);
            color: var(--color-gray-6);
        }
    }
}
</style>
