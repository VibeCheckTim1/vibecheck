<script setup lang="ts">
import {computed, ref} from "vue";
import type {useFormField} from "../composables/useFormField.ts";

const props = withDefaults(defineProps<{
    control: ReturnType<typeof useFormField<string>>;
    label?: string;
    placeholder?: string;
    multiline?: boolean;
    type?: string;
    disabled?: boolean;
    required?: boolean;
    helpMessage?: string;
    autocomplete?: string;
}>(), {
    multiline: false,
    type: "text",
    disabled: false,
    required: true,
    autocomplete: "off",
});

const uniqueId = `form-field-${Math.random().toString(36).slice(2, 9)}`;

const isFocused = ref<boolean>(false);

const model = computed<string>({
    get() {
        const controlValue = props.control.getInputValue();
        if (controlValue === null || controlValue === undefined) {
            return "";
        }
        return controlValue;
    },
    set(newValue: string) {
        if (props.disabled) {
            return;
        }

        if (newValue.trim() === "") {
            props.control.setInputValue(null);
        }
        else {
            props.control.setInputValue(newValue);
        }
    },
});

function onBlur(): void {
    isFocused.value = false;
    if (props.control.markAsTouched) {
        props.control.markAsTouched();
    }
    if (props.control.validate) {
        props.control.validate();
    }
}
</script>

<template>
    <div class="form-field-holder">
        <label :for="uniqueId" v-if="label">
            {{ label }}
            <span v-if="!props.required">(optional)</span>
        </label>

        <div class="text-input-holder"
             @blur="onBlur"
             :class="{ 'invalid': props.control.errors.value.length > 0, 'disabled': props.disabled, 'active': isFocused }">
            <div class="input-addon prefix" v-if="$slots.prefix">
                <slot name="prefix"/>
            </div>

            <textarea v-if="multiline"
                      :id="uniqueId"
                      v-model.trim="model"
                      :placeholder="placeholder || label"
                      rows="4"
                      @click="isFocused = true"
                      @blur="onBlur"
                      :disabled="disabled"/>

            <input v-else
                   :id="uniqueId"
                   v-model="model"
                   :type="props.type"
                   :placeholder="placeholder || label"
                   :autocomplete="props.autocomplete"
                   @click="isFocused = true"
                   @blur="onBlur"
                   :disabled="disabled"/>

            <div class="input-addon suffix" v-if="$slots.suffix">
                <slot name="suffix"/>
            </div>
        </div>

        <ul class="form-control-errors" v-if="props.control.errors.value.length > 0 && !props.disabled">
            <li v-for="error in props.control.errors.value" :key="error">{{ error }}</li>
        </ul>

        <p class="form-field-help" v-if="props.helpMessage">{{ props.helpMessage }}</p>
    </div>
</template>

<style scoped>
.text-input-holder {
    display: flex;
    align-items: stretch;
    width: 100%;
    position: relative;
    cursor: text;
    border-radius: var(--border-radius-5);
    border: 1px solid var(--color-gray-2);
    transition: border-color 150ms ease, background-color 150ms ease, box-shadow 150ms ease;
    background-color: var(--color-gray-0);
    padding-inline: var(--spacing-2);

    &:hover {
        border-color: var(--color-gray-4);
    }

    &.active,
    &:focus {
        outline: none;
        border-color: var(--color-gray-5);
        box-shadow: 0 0 0 3px var(--color-gray-1);
    }

    &.invalid {
        border-color: var(--color-red-2);
        background-color: var(--color-red-0);

        @media screen and (hover: hover) {
            &:hover {
                border-color: var(--color-red-4);
            }
        }

        &.active,
        &:focus {
            outline: none;
            border-color: var(--color-red-5);
            box-shadow: 0 0 0 3px var(--color-red-1);
        }
    }

    & > *:first-child {
        border-bottom-left-radius: var(--border-radius-5);
        border-top-left-radius: var(--border-radius-5);
    }

    & > *:last-child {
        border-bottom-right-radius: var(--border-radius-5);
        border-top-right-radius: var(--border-radius-5);
    }

    .input-addon {
        background: transparent;
        display: flex;
        align-items: center;
        color: var(--color-gray-4);
        height: 42px;

        &.prefix {
            padding-left: var(--spacing-0);
        }

        &.suffix {
            padding-right: var(--spacing-0);
        }
    }

    input,
    textarea {
        width: 100%;
        background: transparent;
        padding-inline: var(--spacing-1);
        font-size: var(--font-size-2);
        color: var(--color-gray-8);
        outline: none;
        min-height: 45px;
        resize: none;
        line-height: 1.5;
        border: none;
        font-family: "Helvetica", sans-serif;

        &:is(textarea) {
            padding-block: var(--spacing-1);
        }

        &::placeholder {
            color: var(--color-gray-6);
            opacity: 1;
        }
    }
}
</style>
