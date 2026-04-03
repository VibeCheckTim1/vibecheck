<script setup lang="ts">

import {useDialog} from "../composables/useDialog.ts";

const dialog = useDialog();
</script>

<template>
    <teleport to="body">
        <div class="dialogs-outlet">
            <div v-for="(dialogItem) in dialog.dialogs.value"
                 :key="dialogItem.id"
                 class="dialog">
                <transition name="dialog-container" appear>
                    <div class="dialog-container">
                        <div class="dialog-header">
                            <h1 class="dialog-title">{{ dialogItem.title }}</h1>
                            <button class="dialog-close-button"
                                    type="button"
                                    @click="dialog.closeDialog(dialogItem.id)">
                                <i class="icon-close"></i>
                            </button>
                        </div>

                        <div class="dialog-body">
                            <component :is="dialogItem.component" v-bind="dialogItem.props"/>
                        </div>
                    </div>
                </transition>
            </div>
        </div>
    </teleport>
</template>

<style scoped>
.dialog {
    position: fixed;
    inset: 0;
    z-index: 50;
    width: 100%;
    height: 100%;
    background-color: transparent;

    @media screen and (min-width: 768px) {
        display: flex;
        align-items: center;
        justify-content: center;
    }

    &::before {
        position: absolute;
        inset: 0;
        content: '';
        background-color: rgba(30, 56, 85, 0.5);
    }

    .dialog-container {
        position: fixed;
        z-index: 1;
        left: 0;
        right: 0;
        bottom: 0;
        display: flex;
        flex-direction: column;
        background-color: white;
        width: 100dvw;
        max-width: 100dvw;
        height: 96dvh;
        overflow-y: auto;
        -ms-overflow-style: none;
        scrollbar-width: none;
        border-top-left-radius: var(--spacing-5);
        border-top-right-radius: var(--spacing-5);
        corner-shape: squircle;
        margin-top: 4dvh;

        @media screen and (min-width: 768px) {
            position: static;
            box-shadow: 0 0 var(--spacing-4) var(--color-gray-6);
            border-radius: var(--spacing-6);
            corner-shape: squircle;
            width: 32rem;
            max-width: 32rem;
            height: fit-content;
            overflow-y: visible;
            margin-top: 0;
        }

        .dialog-header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding-inline: var(--spacing-3);
            padding-block: var(--spacing-2);
            background-color: inherit;
            position: sticky;
            top: 0;
            z-index: 1;
            border-bottom: 1px solid var(--color-gray-2);
            border-top-left-radius: var(--spacing-5);
            border-top-right-radius: var(--spacing-5);
            corner-shape: squircle;

            .dialog-title {
                font-size: var(--font-size-2);
                pointer-events: none;
                font-weight: bold;
                margin: 0;
                color: var(--color-gray-8);
            }

            .dialog-close-button {
                background: transparent;
                border: none;
                border-radius: var(--spacing-3);
                corner-shape: squircle;
                width: 2.25rem;
                height: 2.25rem;
                display: inline-flex;
                align-items: center;
                justify-content: center;
                cursor: pointer;
                color: var(--color-gray-5);
                font-size: var(--font-size-4);
                outline: none;
                transition: background-color 150ms ease, color 150ms ease, transform 100ms ease;

                &:hover {
                    background-color: var(--color-gray-0);
                    color: var(--color-gray-6);
                }

                &:active {
                    background-color: var(--color-gray-1);
                    transform: scale(0.95);
                }
            }
        }

        .dialog-body {
            flex: 1;
            padding-bottom: var(--spacing-5);

            @media screen and (min-width: 768px) {
                padding-bottom: unset;
            }
        }
    }
}

.dialogs-outlet {
    @media screen and (min-width: 768px) {
        &:deep(.dialog:nth-of-type(2)) .dialog-container {
            width: 41rem;
            max-width: 41rem;
        }

        &:deep(.dialog:nth-of-type(3)) .dialog-container {
            width: 40rem;
            max-width: 40rem;
        }

        &:deep(.dialog:nth-of-type(4)) .dialog-container {
            width: 39rem;
            max-width: 39rem;
        }

        &:deep(.dialog:nth-of-type(5)) .dialog-container {
            width: 38rem;
            max-width: 38rem;
        }
    }

    .dialog-container-enter-active {
        transition: transform 250ms cubic-bezier(0.16, 1, 0.3, 1),
        opacity 250ms ease-out;
    }

    .dialog-container-leave-active {
        transition: transform 50ms ease-in,
        opacity 50ms ease-in;
    }

    .dialog-container-enter-from,
    .dialog-container-leave-to {
        opacity: 0;
    }

    .dialog-container-enter-to,
    .dialog-container-leave-from {
        opacity: 1;
    }

    .dialog-container-enter-from,
    .dialog-container-leave-to {
        transform: translateY(100%);
    }

    .dialog-container-enter-to,
    .dialog-container-leave-from {
        transform: translateY(0);
    }

    @media screen and (min-width: 768px) {
        .dialog-container-enter-from,
        .dialog-container-leave-to {
            transform: scale(0.92) translateY(24px);
        }

        .dialog-container-enter-to,
        .dialog-container-leave-from {
            transform: scale(1) translateY(0);
        }
    }
}
</style>
