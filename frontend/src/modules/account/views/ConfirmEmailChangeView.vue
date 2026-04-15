<script setup lang="ts">
import {useForm} from '../../../composables/useForm';
import {useFormField} from '../../../composables/useFormField';
import {ApiError} from '../../../composables/useHttpClient';
import {useState} from '../../../composables/useState';
import {useToast} from '../../../composables/useToast';
import {useValidators} from '../../../composables/useValidators';
import router from '../../../router';
import {useAccountService} from '../composables/useAccountService.ts';
import InputText from '../../../components/InputText.vue';
import {useRoute} from 'vue-router';
import {useDialog} from '../../../composables/useDialog';
import ChangeEmailForm from '../components/ChangeEmailForm.vue';
import {computed} from 'vue';

const route = useRoute();
const newEmail = computed(() => route.query.email as string);

const {currentUser, setUser} = useState();
const {required} = useValidators();
const {showSuccess, showError} = useToast();

const {openDialog} = useDialog();

const form = useForm({
    code: useFormField<string>(null, [required]),
});

async function submitForm() {
    if (!form.validateForm() || !currentUser.value) {
        return;
    }
    try {
        const {confirmMailEdit} = useAccountService();

        const user = await confirmMailEdit({
            code: form.code.getInputValue()
        });

        setUser(user);
        showSuccess("Email changed successfully!");

        await router.push({
            name: "updateProfile"
        });

    }
    catch (error) {
        if (error instanceof ApiError) {
            showError(error.message);
            return;
        }
        showError("Something went wrong.");
    }
}

async function resendCode() {
    if (!currentUser.value) {
        return;
    }
    try {
        const {emailVerificationCodeAction} = useAccountService();

        await emailVerificationCodeAction({newEmail: newEmail.value});

        showSuccess("Verification code sent again!");
    }
    catch (error) {
        if (error instanceof ApiError) {
            showError(error.message);
            return;
        }
        showError("Failed to resend code.");
    }
}

function useDifferentEmail() {
    openDialog(ChangeEmailForm, "Change email", {
        callback: async (newEmail: string) => {
            await router.replace({
                name: "confirmEmailChange",
                query: {email: newEmail}
            });
        }
    });
}


</script>

<template>
    <div class="center-content-container">
        <div class="verify-email-container">
            <div class="status-icon">
                <span class="checkmark">✓</span>
            </div>

            <div class="verify-email-header">
                <span class="title">Check your email</span>
                <span class="description">
                    We’ve sent a 6-digit verification code to
                </span>
                <span class="email">{{ newEmail }}</span>
            </div>

            <form @submit.prevent="submitForm" class="verify-email-form">
                <div class="verification-code-field">
                    <InputText :control="form.code" label="Verification code" placeholder="000000"
                               help-message="Enter the 6-digit code from your email"/>
                </div>

                <button class="primary-button verify-button" type="submit">
                    Verify email
                </button>
            </form>

            <div class="verify-email-actions">
                <span class="secondary-text">Didn’t receive the code?</span>
                <button type="button" class="decorative-link" @click="resendCode">Resend code</button>
                <button type="button" class="secondary-link" @click="useDifferentEmail">Use a different email</button>
            </div>
        </div>
    </div>
</template>

<style scoped>
.verify-email-form {
    width: 100%;
    display: flex;
    flex-direction: column;
    gap: var(--spacing-4);

    .verification-code-field {
        width: 100%;
    }

    .verify-button {
        width: 100%;
    }
}

.checkmark {
    font-size: 28px;
    color: var(--color-primary-4);
}

.verify-email-container {
    width: 100%;
    max-width: 460px;
    margin: 0 auto;
    padding-block: var(--spacing-10);
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: var(--spacing-6);

    .status-icon {
        width: 64px;
        height: 64px;
        border-radius: 50%;
        background-color: var(--color-primary-0);
        display: flex;
        align-items: center;
        justify-content: center;
    }

    .verify-email-header {
        display: flex;
        flex-direction: column;
        align-items: center;
        text-align: center;
        gap: var(--spacing-1);

        .title {
            font-size: var(--font-size-5);
            font-weight: bold;
            color: var(--color-gray-8);
        }

        .description {
            font-size: var(--font-size-1);
            color: var(--color-gray-6);
        }

        .email {
            font-size: var(--font-size-1);
            color: var(--color-primary-4);
            font-weight: 600;
        }
    }

    .verify-email-form {
        width: 100%;
        display: flex;
        flex-direction: column;
        gap: var(--spacing-4);

        .verify-button {
            width: 100%;
        }
    }

    .verify-email-actions {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: var(--spacing-2);

        .secondary-text {
            font-size: var(--font-size-1);
            color: var(--color-gray-5);
        }

        .secondary-link {
            background: none;
            border: none;
            padding: 0;
            cursor: pointer;
            font-size: var(--font-size-1);
            color: var(--color-gray-6);

            &:hover {
                color: var(--color-gray-8);
                text-decoration: underline;
            }
        }
    }
}
</style>
