<script setup lang="ts">
import {useDialog} from '../../../composables/useDialog';
import {useForm} from '../../../composables/useForm';
import {useFormField} from '../../../composables/useFormField';
import {useState} from '../../../composables/useState';
import {useValidators} from '../../../composables/useValidators';
import {computed} from 'vue';
import {useAccountService} from '../composables/useAccountService.ts';
import {ApiError} from '../../../composables/useHttpClient';
import InputText from '../../../components/InputText.vue';
import {useToast} from '../../../composables/useToast';

const props = defineProps<{
    callback: () => Promise<void>;
}>();

const {currentUser} = useState();
const {closeDialog} = useDialog();
const {required} = useValidators();
const {showError} = useToast();

const form = useForm({
    oldPassword: useFormField<string>("", [required]),
    newPassword: useFormField<string>("", [required]),
    confirmPassword: useFormField<string>("", [required]),
});

const passwordsDoNotMatch = computed(() => {
    return form.newPassword.getInputValue() !== form.confirmPassword.getInputValue();
});

async function submitForm() {
    if (form.validateForm() && currentUser.value && !passwordsDoNotMatch.value) {
        try {
            const {changePasswordAction} = useAccountService();

            await changePasswordAction({
                oldPassword: form.oldPassword.getInputValue(),
                newPassword: form.newPassword.getInputValue(),
            });

            await props.callback();
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
}

</script>


<template>
    <div class="change-password-container">
        <form @submit.prevent="submitForm">
            <InputText :control="form.oldPassword"
                       label="Current password"
                       type="password"
                       autocomplete="new-password"/>

            <InputText :control="form.newPassword"
                       label="New password"
                       type="password"/>

            <InputText :control="form.confirmPassword"
                       label="Confirm new password"
                       type="password"/>

            <p v-if="passwordsDoNotMatch" class="form-error">Passwords do not match.</p>

            <button type="submit" class="primary-button large-button">Save</button>
        </form>
    </div>
</template>

<style scoped>
.change-password-container {
    padding: var(--spacing-4);
}

.form-error {
    color: var(--color-red-5);
    font-size: var(--font-size-1);
    margin-top: var(--spacing-1);
    margin-bottom: var(--spacing-3);
}
</style>
