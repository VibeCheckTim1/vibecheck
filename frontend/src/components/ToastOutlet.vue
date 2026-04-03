<script setup lang="ts">
import {useToast} from "../composables/useToast.ts";

const {toasts, removeToast} = useToast();
</script>

<template>
    <div class="toast-container">
        <TransitionGroup name="toast" tag="div" class="toast-list-holder">
            <div v-for="toast in toasts" :key="toast.id" class="toast-message-container" :class="toast.type">
                <div class="toast-icon-holder">
                    <i class="icon-check-circle-outline" v-if="toast.type === 'success'"></i>
                    <i class="icon-close-circle-outline" v-else-if="toast.type === 'error'"></i>
                    <i class="icon-information-circle-outline" v-else></i>
                </div>
                <p class="toast-content">{{ toast.message }}</p>
                <button type="button" class="hide-toast-button" @click="removeToast(toast.id)">
                    <i class="icon-close close-icon"></i>
                </button>
            </div>
        </TransitionGroup>
    </div>
</template>

<style scoped>
.toast-container {
    position: fixed;
    top: 0;
    right: 0;
    z-index: 55;
    padding: var(--spacing-2);
    user-select: none;
    max-height: 100dvh;
    overflow-y: auto;
    -ms-overflow-style: none;
    scrollbar-width: none;
    width: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: flex-start;
    pointer-events: none;

    &::-webkit-scrollbar {
        display: none;
    }

    .toast-list-holder {
        pointer-events: all;
        width: 100%;

        @media screen and (min-width: 768px) {
            width: 75%;
        }

        @media screen and (min-width: 1200px) {
            width: 480px;
        }
    }

    .toast-message-container {
        display: flex;
        margin-bottom: var(--spacing-2);
        align-items: flex-start;
        padding-inline: var(--spacing-1);
        padding-block: var(--spacing-2);
        gap: var(--spacing-1);
        border-radius: var(--spacing-5);
        corner-shape: squircle;
        box-shadow: 0 2px 8px var(--color-gray-5);
        width: 100%;
        position: relative;
        pointer-events: auto;
        opacity: 1;
        transition: transform 150ms ease, box-shadow 150ms ease;

        .toast-icon-holder {
            border-radius: var(--spacing-2);
            corner-shape: squircle;
            width: var(--spacing-8);
            height: var(--spacing-8);
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            flex-basis: var(--spacing-8);
            flex-shrink: 0;
            pointer-events: none;
            user-select: none;

            i {
                font-size: var(--spacing-5);
                line-height: var(--spacing-5);
            }
        }

        .toast-content {
            line-height: var(--spacing-3);
            min-height: var(--spacing-8);
            display: flex;
            align-items: center;
            flex: 1 1 auto;
            color: white;
            word-break: break-word;
            overflow-wrap: anywhere;
            white-space: normal;
            pointer-events: none;
            user-select: none;
            font-size: var(--font-size-1);
        }

        .hide-toast-button {
            background: transparent;
            border: none;
            border-radius: var(--spacing-2);
            corner-shape: squircle;
            width: var(--spacing-8);
            height: var(--spacing-8);
            display: inline-flex;
            align-items: center;
            justify-content: center;
            flex-basis: var(--spacing-8);
            flex-shrink: 0;
            cursor: pointer;
            color: white;
            outline: none;
        }

        &.info {
            background-color: var(--color-primary-4);
        }

        &.error {
            background-color: var(--color-red-4);
        }

        &.success {
            background-color: var(--color-green-4);
        }
    }
}

.toast-enter-active {
    animation: bounceDown 250ms ease-out forwards;

    @media screen and (min-width: 1200px) {
        animation: bounceUp 250ms ease-out forwards;
    }
}

.toast-leave-active {
    animation: toastFadeOut 250ms ease-in forwards;
}

@keyframes bounceDown {
    0% {
        transform: translateY(-120%) scale(0.85);
        opacity: 0;
    }
    60% {
        transform: translateY(12%) scale(1.02);
        opacity: 1;
    }
    100% {
        transform: translateY(0) scale(1);
        opacity: 1;
    }
}

@keyframes bounceUp {
    0% {
        transform: translateY(120%) scale(0.85);
        opacity: 0;
    }
    60% {
        transform: translateY(-12%) scale(1.02);
        opacity: 1;
    }
    100% {
        transform: translateY(0) scale(1);
        opacity: 1;
    }
}

@keyframes toastFadeOut {
    0% {
        opacity: 1;
        transform: translateY(0) scale(1);
    }
    100% {
        opacity: 0;
        transform: translateY(-20%) scale(0.9);
    }
}
</style>
