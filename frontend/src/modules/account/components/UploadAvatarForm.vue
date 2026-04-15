<script setup lang="ts">
import {computed, ref} from "vue";
import {useAccountService} from "../composables/useAccountService.ts";
import {useState} from "../../../composables/useState.ts";
import type {User} from "../../../entities/user.ts";

const props = defineProps<{
    callback: (user: User) => Promise<void>;
}>();

const inputFile = ref<HTMLInputElement | null>(null);
const isDragOver = ref(false);
const file = ref<File | null>(null);

const errors = ref({
    count: false,
    format: false,
    size: false,
    required: false,
});

const hasError = computed(() => Object.values(errors.value).some(Boolean));

const allowedExtensions = ["jpg", "jpeg", "png", "svg"];
const maxSize = 10 * 1024 * 1024; // 10 MB

function resetErrors() {
    errors.value = {
        count: false,
        format: false,
        size: false,
        required: false,
    };
}

function handleDrop(e: DragEvent) {
    isDragOver.value = false;
    handleFiles(e.dataTransfer?.files);
}

function handleSelect(e: Event) {
    const target = e.target as HTMLInputElement;
    handleFiles(target.files);
}

function handleFiles(files?: FileList | null) {
    resetErrors();

    if (!files || files.length === 0) {
        errors.value.required = true;
        return;
    }

    if (files.length > 1) {
        errors.value.count = true;
        return;
    }

    const selectedFile = files[0];
    const extension = selectedFile.name.split(".").pop()?.toLowerCase();

    if (!extension || !allowedExtensions.includes(extension)) {
        errors.value.format = true;
        return;
    }

    if (selectedFile.size > maxSize) {
        errors.value.size = true;
        return;
    }

    file.value = selectedFile;
}

function clearFile() {
    file.value = null;
    resetErrors();
}

function validate(): boolean {
    resetErrors();

    if (!file.value) {
        errors.value.required = true;
        return false;
    }

    return true;
}

async function submitForm() {
    const {currentUser} = useState();
    if (validate() && currentUser.value) {
        const formData = new FormData();
        formData.append("file", file.value as File);

        try {
            const {uploadAvatarAction} = useAccountService();
            const user = await uploadAvatarAction(formData);
            await props.callback(user);
        }
        catch (error) {
            console.error(error);
        }
    }
}
</script>

<template>
    <div class="avatar-container">
        <div class="form-field-holder">
            <div class="upload-wrap"
                 :class="{ 'drag-over': isDragOver, 'invalid': hasError }"
                 v-if="!file"
                 @dragenter.prevent="isDragOver = true"
                 @dragover.prevent
                 @dragleave.prevent="isDragOver = false"
                 @drop.prevent="handleDrop"
                 @click="inputFile?.click()">
                <input ref="inputFile" type="file" @change="handleSelect"/>
                <i class="icon-upload-box-outline"/>
                <p>Drag and drop a file or click to select</p>
            </div>

            <div class="uploaded-file-info" v-else>
                <span>{{ file.name }}</span>
                <button type="button" class="clear-files" @click="clearFile">
                    <i class="icon-close"></i>
                </button>
            </div>

            <ul class="form-control-errors" v-if="hasError">
                <li v-if="errors.count">Only one file can be attached</li>
                <li v-if="errors.format">Supported formats are {{ allowedExtensions.join(", ") }}</li>
                <li v-if="errors.size">File must not exceed 10 MB</li>
                <li v-if="errors.required">File is required</li>
            </ul>

            <p class="form-field-help">Supported formats are {{ allowedExtensions.join(", ") }}</p>
        </div>

        <br>
        <button type="button" class="primary-button large-button" @click="submitForm">
            Save
        </button>
    </div>
</template>

<style scoped>
.avatar-container {
    padding: var(--spacing-4);

    .upload-wrap {
        padding: var(--spacing-5);
        display: flex;
        cursor: pointer;
        align-items: center;
        justify-content: center;
        flex-direction: column;
        border-radius: var(--spacing-2);
        background-color: var(--color-gray-0);
        border: 2px dashed var(--color-gray-2);

        &:hover {
            background-color: var(--color-primary-0);
        }

        &.invalid {
            border-color: var(--color-red-5);
        }

        &.drag-over {
            border-color: var(--color-primary-4);
            outline: 2px solid var(--color-primary-4);
            outline-offset: 2px;
        }

        input {
            display: none;
            pointer-events: none;
        }

        i {
            font-size: var(--font-size-8);
            pointer-events: none;
            color: var(--color-primary-4);
        }

        p {
            display: block;
            margin-top: var(--spacing-2);
            font-size: 0.9rem;
            pointer-events: none;
            text-align: center;
        }
    }

    .uploaded-file-info {
        background-color: var(--color-gray-0);
        border: 1px solid var(--color-gray-2);
        border-radius: var(--spacing-3);
        color: var(--color-gray-6);
        min-height: 2.35em;
        outline: none;
        padding-inline: 0.75em 0.25rem;
        display: flex;
        align-items: center;
        justify-content: space-between;

        .clear-files {
            color: var(--color-red-5);
            width: unset;
            height: unset;
            padding: 4px;
            background-color: transparent;
            outline: unset !important;
            cursor: pointer;
            display: flex;
            align-items: center;
            justify-content: center;
            outline: none;
            border: none;

            i {
                font-size: 1.5rem;
            }
        }
    }
}
</style>
