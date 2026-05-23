<script setup lang="ts">
import PageHeaderComponent from "../../../components/PageHeaderComponent.vue";
import InputText from "../../../components/InputText.vue";
import InputToggle from "../../../components/InputToggle.vue";
import {useForm} from "../../../composables/useForm.ts";
import {useFormField} from "../../../composables/useFormField.ts";
import {useValidators} from "../../../composables/useValidators.ts";
import {useToast} from "../../../composables/useToast.ts";
import {ApiError} from "../../../composables/useHttpClient.ts";
import {usePlaylistService} from "../../playlists/composables/usePlaylistService.ts";
import {useState} from "../../../composables/useState.ts";
import {useRouter} from "vue-router";

const {required, maxLength} = useValidators();
const {showSuccess, showError} = useToast();
const {currentUser} = useState();
const router = useRouter();

const form = useForm({
    name: useFormField<string>("", [required, maxLength(100)]),
    isFavorite: useFormField<boolean>(false),
    isPublic: useFormField<boolean>(true),
});

async function submitForm() {
    if (!form.validateForm()) {
        return;
    }

    try {
        const {createPlaylist} = usePlaylistService();
        await createPlaylist(form.toJson());
        showSuccess("Playlist created successfully!");

        if (currentUser.value) {
            await router.push({
                name: "account",
                params: {
                    userId: currentUser.value.idUser
                }
            });
        }
        else {
            form.resetForm();
        }
    }
    catch (error) {
        if (error instanceof ApiError) {
            showError(error.message);
        }
    }
}
</script>

<template>
    <PageHeaderComponent>
        <span>Create playlist</span>
    </PageHeaderComponent>
    <div class="center-content-container">
        <form class="create-playlist-form" @submit.prevent="submitForm">
            <InputText :control="form.name" label="Playlist name" placeholder="My new playlist"/>

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

            <div class="form-bottom">
                <button class="primary-button large-button" type="submit">Create playlist</button>
            </div>
        </form>
    </div>
</template>

<style scoped>
.create-playlist-form {
    padding-block: var(--spacing-5);
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
