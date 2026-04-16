<script setup lang="ts">
import { computed } from 'vue';
import { useDialog } from '../../../composables/useDialog';
import { useForm } from '../../../composables/useForm';
import { useFormField } from '../../../composables/useFormField';
import { useState } from '../../../composables/useState';
import { useToast } from '../../../composables/useToast';
import { useValidators } from '../../../composables/useValidators';
import { useProfileService } from '../composables/useProfileService';
import { ApiError } from '../../../composables/useHttpClient';
import InputText from '../../../components/InputText.vue';

const props = defineProps<{
    callback: (newEmail: string) => Promise<void>;
}>();



const { currentUser } = useState();
const { closeDialog } = useDialog();
const { required, email } = useValidators();
const { showError } = useToast();


const form = useForm({
    oldEmail: useFormField<string>(currentUser.value?.email),
    newEmail: useFormField<string>(null, [required, email]),
});

const canSubmit = computed(() => {
    return (
        !!form.newEmail.getInputValue()
    );
})


async function submitForm() {
    if (!form.validateForm() || !currentUser.value) {
        return;
    }

    try {
        const { emailVerificationCodeAction } = useProfileService(currentUser.value.idUser);
        const newEmail = form.newEmail.getInputValue();
        await emailVerificationCodeAction({ newEmail });

        await props.callback(newEmail);
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
    <div class="change-password-container">
        <form @submit.prevent="submitForm">
            <InputText :control="form.oldEmail" label="Old Email" type="email" :disabled="true"/>

            <InputText :control="form.newEmail" label="New Email" type="email"/>



            <button type="submit" class="primary-button large-button" :disabled="!canSubmit">
                Send verification code
            </button>
        </form>
    </div>
</template>


<style scoped>
.change-password-container {
    padding: var(--spacing-4);
}

.primary-button:disabled {
    background-color: #e0e0e0;
    color: #9e9e9e;
    cursor: not-allowed;
    box-shadow: none;
    transform: none;
    opacity: 0.6;
    border: #9e9e9e;
}

.form-error {
    color: var(--color-red-5);
    font-size: var(--font-size-1);
    margin-top: var(--spacing-1);
    margin-bottom: var(--spacing-3);
}
</style>