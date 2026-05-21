<script setup lang="ts">

import {useForm} from "../../../composables/useForm.ts";
import {useFormField} from "../../../composables/useFormField.ts";
import {useValidators} from "../../../composables/useValidators.ts";
import InputText from "../../../components/InputText.vue";
import {useToast} from "../../../composables/useToast.ts";
import {useLoginService} from "../composables/useLoginService.ts";
import {ApiError} from "../../../composables/useHttpClient.ts";
import {API_BASE_URL} from "../../../config.ts";
import {useRouter, useRoute} from "vue-router";
import {useState} from "../../../composables/useState.ts";
import {onMounted} from "vue";

const {required} = useValidators();
const {showError} = useToast();
const {setUser} = useState();
const router = useRouter();
const route = useRoute();

const form = useForm({
    username: useFormField<string>(null, [required]),
    password: useFormField<string>(null, [required]),
});

onMounted(() => {
    if (route.query.oauth2Error) {
        showError("OAuth authentication failed. Please try again.");
    }
});

async function submitForm() {
    if (form.validateForm()) {
        try {
            const {loginAction} = useLoginService();
            const user = await loginAction(form.toJson());
            setUser(user);
            await router.push({
                name: "home"
            });
        }
        catch (error) {
            if (error instanceof ApiError) {
                showError(error.message);
            }
        }
    }
}

function redirectToGoogleOAuth() {
    window.location.href = `${API_BASE_URL}/oauth2/authorization/google`;
}

function redirectToSpotifyOAuth() {
    window.location.href = `${API_BASE_URL}/oauth2/authorization/spotify`;
}
</script>

<template>
    <div class="login-page-container">
        <div class="login-form-holder">
            <div class="logo-holder">
                <svg width="64" height="64" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path
                        d="M0 16C0 7.16344 7.16344 0 16 0H48C56.8366 0 64 7.16344 64 16V48C64 56.8366 56.8366 64 48 64H16C7.16344 64 0 56.8366 0 48V16Z"
                        fill="#7C3AED"/>
                    <path d="M25.5 45V16.8333L51.5 12.5V40.6667" stroke="white" stroke-width="2.66667" stroke-linecap="round"
                          stroke-linejoin="round"/>
                    <path
                        d="M19 51.5C22.5899 51.5 25.5 48.5899 25.5 45C25.5 41.4101 22.5899 38.5 19 38.5C15.4101 38.5 12.5 41.4101 12.5 45C12.5 48.5899 15.4101 51.5 19 51.5Z"
                        stroke="white" stroke-width="2.66667" stroke-linecap="round" stroke-linejoin="round"/>
                    <path
                        d="M45 47.1667C48.5899 47.1667 51.5 44.2565 51.5 40.6667C51.5 37.0768 48.5899 34.1667 45 34.1667C41.4101 34.1667 38.5 37.0768 38.5 40.6667C38.5 44.2565 41.4101 47.1667 45 47.1667Z"
                        stroke="white" stroke-width="2.66667" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
            </div>

            <h1 class="app-name">VibeCheck</h1>
            <p class="app-description">Share your music, discover new vibes</p>

            <form @submit.prevent="submitForm">
                <InputText :control="form.username"
                           label="Username"/>
                <InputText :control="form.password"
                           type="password"
                           label="Password"/>
                <a href="#" class="decorative-link">Forgot your password?</a>
                <button name="Login" type="submit" class="primary-button large-button">Login</button>
            </form>

            <div class="divider">
                <hr class="divider-line"/>
                <span class="divider-text">or continue with</span>
                <hr class="divider-line"/>
            </div>

            <div class="sso-options-container">
                <button class="secondary-button large-button" type="button" @click="redirectToGoogleOAuth">
                    <strong>Google</strong>
                </button>
                <button class="secondary-button large-button" type="button" @click="redirectToSpotifyOAuth">
                    <strong>Spotify</strong>
                </button>
            </div>

            <div class="login-footer">
                <span>Don't have an account?</span>
                <router-link :to="{name: 'register'}" class="decorative-link">Sign up</router-link>
            </div>
        </div>
    </div>
</template>

<style scoped>
.login-page-container {
    width: 100%;
    height: 100dvh;
    display: flex;
    align-items: center;
    justify-content: center;

    .login-form-holder {
        display: flex;
        align-items: center;
        justify-content: center;
        flex-direction: column;
        width: 480px;
        padding: var(--spacing-4);

        .app-name {
            width: 100%;
            text-align: center;
            font-size: var(--font-size-7);
        }

        .app-description {
            width: 100%;
            text-align: center;
            color: var(--color-gray-4);
        }

        form {
            margin-top: var(--spacing-6);
        }

        .divider {
            display: flex;
            align-items: center;
            justify-content: center;
            gap: var(--spacing-2);
            margin-top: var(--spacing-6);
            margin-bottom: var(--spacing-6);
            width: 100%;

            .divider-text {
                font-size: var(--font-size-1);
                color: var(--color-gray-5);
            }

            .divider-line {
                flex-grow: 1;
                border: none;
                border-top-width: 1px;
                border-top-style: solid;
                border-top-color: var(--color-gray-2);
            }
        }

        .sso-options-container {
            width: 100%;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: var(--spacing-4);

            button {
                flex: 1;
                flex-shrink: 0;
            }
        }

        .login-footer {
            display: flex;
            align-items: center;
            justify-content: center;
            gap: var(--spacing-1);
            font-size: var(--font-size-1);
            margin-top: var(--spacing-8);

            span {
                color: var(--color-gray-5);
            }
        }

        .decorative-link {
            color: var(--color-primary-4);
            font-weight: bold;
            text-decoration-line: none;
            font-size: var(--font-size-1);

            &:hover {
                text-decoration-line: underline;
            }
        }
    }
}
</style>
