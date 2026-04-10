<script setup lang="ts">
import {useState} from "../../../composables/useState.ts";
import PageHeaderComponent from "../../../components/PageHeaderComponent.vue";
import {useValidators} from "../../../composables/useValidators.ts";
import {useToast} from "../../../composables/useToast.ts";
import {useForm} from "../../../composables/useForm.ts";
import {useFormField} from "../../../composables/useFormField.ts";
import {ApiError} from "../../../composables/useHttpClient.ts";
import InputText from "../../../components/InputText.vue";
import InputToggle from "../../../components/InputToggle.vue";
import {useProfileService} from "../composables/useProfileService.ts";
import {useRouter} from "vue-router";
import UploadAvatarForm from "../components/UploadAvatarForm.vue";
import {useDialog} from "../../../composables/useDialog.ts";
import type {User} from "../../../entities/user.ts";
import {useConfirm} from "../../../composables/useConfirm.ts";
import ChangePasswordForm from "../components/ChangePasswordForm.vue";

const {currentUser, setUser} = useState();
const {openDialog} = useDialog();
const {required} = useValidators();
const {showSuccess, showError} = useToast();
const {openConfirm} = useConfirm();
const router = useRouter();

function uploadAvatar() {
    openDialog(UploadAvatarForm, "Upload profile picture", {
        callback: async (user: User) => {
            setUser(user);
            showSuccess("Updated successfully!");
            await router.push({
                name: "profile"
            });
        }
    });
}

function changePassword() {
    openDialog(ChangePasswordForm, "Change password", {
        callback: async() => {
            showSuccess("Password changed successfully!");
        }
    })
}

const form = useForm({
    firstName: useFormField<string>(currentUser.value?.firstName, [required]),
    lastName: useFormField<string>(currentUser.value?.lastName, [required]),
    username: useFormField<string>(currentUser.value?.username, [required]),
    bio: useFormField<string>(currentUser.value?.bio, [required]),
    isPrivate: useFormField<boolean>(currentUser.value?.isPrivate),
});

async function submitForm() {
    if (form.validateForm() && currentUser.value) {
        try {
            const {updateAction} = useProfileService(currentUser.value.idUser);
            const user = await updateAction({
                firstName: form.firstName.getInputValue(),
                lastName: form.lastName.getInputValue(),
                username: form.username.getInputValue(),
                bio: form.bio.getInputValue(),
                visibility: form.isPrivate.getInputValue() ? "PRIVATE" : "PUBLIC",
            });
            setUser(user);
            showSuccess("Updated successfully!");
            await router.push({
                name: "profile"
            });
        }
        catch (error) {
            if (error instanceof ApiError) {
                showError(error.message);
            }
        }
    }
}

async function deleteAccount() {
    const confirmed = await openConfirm({
        isDanger: true,
        title: "Delete account",
        subtitle: "Are you sure you want to delete this account?",
        acceptMessage: "Yes, delete account?",
    });

    if (confirmed && currentUser.value) {
        const {deleteAction} = useProfileService(currentUser.value.idUser);
        await deleteAction();
        window.location.href = "/";
    }
}
</script>

<template>
    <PageHeaderComponent>
        <span>Edit profile</span>
        <template #actions>
            <button class="primary-button" type="button" @click="submitForm">Save</button>
        </template>
    </PageHeaderComponent>
    <div class="center-content-container">
        <div class="update-profile-container" v-if="currentUser">
            <div class="user-avatar-holder">
                <img v-if="currentUser.avatarUrl" :src="currentUser.avatarUrl" alt="Avatar">
                <button type="button" class="change-avatar-button" @click="uploadAvatar">
                    <i class="icon-camera"></i>
                </button>
            </div>
            <span class="change-avatar-message">Change profile photo</span>
            <form @submit.prevent="submitForm">
                <InputText :control="form.firstName"
                           label="First name"/>
                <InputText :control="form.lastName"
                           label="Last name"/>
                <InputText :control="form.username"
                           label="Username"
                           help-message="Only letters, numbers, and underscores">
                    <template #prefix>@</template>
                </InputText>
                <InputText :control="form.bio"
                           :multiline="true"
                           placeholder="Tell us about yourself..."
                           label="Bio"/>
                <div class="form-section-container">
                    <div class="icon-holder">
                        <i class="icon-web"></i>
                    </div>
                    <div class="section-info">
                        <span class="title">Private account</span>
                        <span class="description">Everyone can see your playlists</span>
                    </div>
                    <div class="action-holder">
                        <InputToggle :control="form.isPrivate"/>
                    </div>
                </div>
            </form>
            <section>
                <div class="section-title">
                    <span>Account settings</span>
                </div>
                <div class="form-section-container">
                    <div class="icon-holder">
                        <i class="icon-email-outline"></i>
                    </div>
                    <div class="section-info">
                        <span class="title">Email address</span>
                        <span class="description">{{ currentUser.email }}</span>
                    </div>
                    <div class="action-holder">
                        <button class="decorative-link" type="button">Change</button>
                    </div>
                </div>
                <div class="form-section-container">
                    <div class="icon-holder">
                        <i class="icon-lock-outline"></i>
                    </div>
                    <div class="section-info">
                        <span class="title">Password</span>
                        <span class="description">*****</span>
                    </div>
                    <div class="action-holder">
                        <button class="decorative-link" type="button" @click="changePassword">Change</button>
                    </div>
                </div>
                <div class="form-section-container">
                    <div class="icon-holder">
                        <i class="icon-trash-can-outline"></i>
                    </div>
                    <div class="section-info">
                        <span class="title">Delete account</span>
                        <span class="description">Delete your account and all related data</span>
                    </div>
                    <div class="action-holder">
                        <button class="decorative-link" type="button" @click="deleteAccount">Delete</button>
                    </div>
                </div>
            </section>
        </div>
    </div>
</template>

<style scoped>
.update-profile-container {
    padding-block: var(--spacing-10);
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
    width: 100%;

    .user-avatar-holder {
        width: 90px;
        height: 90px;
        border-radius: 50%;
        background-color: var(--color-primary-4);
        display: flex;
        align-items: center;
        justify-content: center;
        margin-bottom: var(--spacing-2);
        position: relative;

        img {
            width: 90px;
            height: 90px;
            border-radius: 50%;
            border: 2px solid var(--color-primary-4);
        }

        .change-avatar-button {
            position: absolute;
            right: 0;
            bottom: 0;
            width: 30px;
            height: 30px;
            border-radius: 50%;
            background-color: var(--color-primary-4);
            border: 3px solid white;
            color: white;
            cursor: pointer;

            &:hover {
                background-color: var(--color-primary-5);
            }
        }
    }

    .change-avatar-message {
        width: 100%;
        text-align: center;
        font-size: var(--font-size-1);
        color: var(--color-gray-3);
        margin-bottom: var(--spacing-2)
    }

    .form-section-container {
        width: 100%;
        background-color: var(--color-gray-0);
        border-radius: var(--border-radius-6);
        padding: var(--spacing-3);
        display: grid;
        grid-template-columns: 30px auto auto;
        gap: var(--spacing-2);

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

        .action-holder {
            display: flex;
            align-items: center;
            justify-content: flex-end;
            padding-right: var(--spacing-2);
        }
    }

    section {
        margin-top: var(--spacing-8);
        width: 100%;
        display: flex;
        flex-direction: column;
        gap: var(--spacing-2);

        .section-title {
            display: flex;
            align-items: center;
            justify-content: flex-start;
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
}
</style>
