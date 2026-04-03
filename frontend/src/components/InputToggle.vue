<script setup lang="ts">
import {computed} from "vue";
import type {useFormField} from "../composables/useFormField.ts";

const props = withDefaults(defineProps<{
    control: ReturnType<typeof useFormField<boolean>>;
    label?: string;
    placeholder?: string;
    disabled?: boolean;
    required?: boolean;
    helpMessage?: string;
}>(), {
    disabled: false,
    required: true,
});

const uniqueId = `form-field-${Math.random().toString(36).slice(2, 9)}`;

const model = computed<boolean>({
    get() {
        return props.control.getInputValue() ?? false;
    },
    set(newValue: boolean) {
        if (props.disabled) {
            return;
        }

        props.control.setInputValue(newValue);
    },
});
</script>

<template>
    <div class="form-field-holder">
        <div class="toggle-holder">
            <div class="toggle-wrap">
                <input type="checkbox"
                       :id="uniqueId"
                       v-model="model"
                       :disabled="disabled"
                       :class="{ invalid: props.control.errors.value.length > 0, disabled: props.disabled }">
            </div>
            <label :for="uniqueId" v-if="props.label">
                {{ label }}
                <span v-if="!props.required">(optional)</span>
            </label>
        </div>

        <ul class="form-control-errors" v-if="props.control.errors.value.length > 0">
            <li v-for="error in props.control.errors.value" :key="error">{{ error }}</li>
        </ul>

        <p class="form-field-help" v-if="props.helpMessage">{{ props.helpMessage }}</p>
    </div>
</template>

<style scoped>
.toggle-holder {
    display: flex;
    align-items: flex-start;
    justify-content: flex-start;

    .toggle-wrap {
        display: flex;
        align-items: center;

        input {
            appearance: none;
            background-color: var(--color-gray-1);
            border-radius: var(--spacing-5);
            corner-shape: round;
            border-style: none;
            flex-shrink: 0;
            height: var(--spacing-7);
            width: 3.75rem;
            margin: 0;
            position: relative;
            cursor: pointer;
            outline: none;
            transition: border-color 150ms ease, background-color 150ms ease, box-shadow 150ms ease;

            &:hover {
                background-color: var(--color-gray-2);
            }

            &:active {
                outline: none;
                border-color: var(--color-primary-4);
            }

            &::before {
                bottom: -6px;
                content: "";
                left: -6px;
                position: absolute;
                right: -6px;
                top: -6px;
            }

            &::after {
                background-color: white;
                border-radius: 50%;
                content: "";
                height: 1.5rem;
                width: 1.5rem;
                top: var(--spacing-1);
                left: var(--spacing-1);
                position: absolute;
                transition: left 50ms ease-out, background-color 50ms ease-out;
            }

            &:checked {
                background-color: var(--color-primary-4);

                &::after {
                    background-color: white;
                    left: var(--spacing-7);
                }

                &:hover {
                    background-color: var(--color-primary-5);
                }

                &:active {
                    background-color: var(--color-primary-5);
                }
            }
        }
    }

    label {
        margin-left: var(--spacing-2);
        color: var(--color-gray-8);
        user-select: none !important;
        line-height: var(--spacing-5) !important;
        font-weight: normal !important;
        cursor: pointer;
    }
}
</style>
