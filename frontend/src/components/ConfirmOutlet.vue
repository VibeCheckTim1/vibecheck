<script setup lang="ts">
import {computed} from "vue";
import {useConfirm} from "../composables/useConfirm.ts";

const confirm = useConfirm();

const iconClass = computed(() => {
    return confirm.iconClass.value ?? "icon-alert-outline";
});

const acceptMessage = computed(() => {
    return confirm.acceptMessage.value ?? "Ok";
});
</script>

<template>
    <teleport to="body">
        <transition name="confirm-backdrop">
            <div v-if="confirm.isVisible.value" class="confirm-dialog">
                <transition name="confirm-container" appear>
                    <div class="confirm-container" v-show="confirm.isVisible.value">
                        <div class="confirm-icon" v-bind:class="{'confirm-danger': confirm.isDanger.value}">
                            <i :class="iconClass"/>
                        </div>
                        <span class="confirm-title">{{ confirm.title.value }}</span>
                        <p v-if="confirm.subtitle.value" class="confirm-subtitle">{{ confirm.subtitle.value }}</p>
                        <div class="confirm-actions">
                            <button type="button" class="secondary-button" @click="confirm.cancel()">Cancel</button>
                            <button type="button"
                                    v-bind:class="{'primary-button': !confirm.isDanger.value, 'danger-button': confirm.isDanger.value}"
                                    @click="confirm.accept()">{{ acceptMessage }}
                            </button>
                        </div>
                    </div>
                </transition>
            </div>
        </transition>
    </teleport>
</template>

<style scoped>
.confirm-dialog {
    position: fixed;
    inset: 0;
    z-index: 50;
    width: 100%;
    height: 100%;
    background-color: transparent;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: var(--spacing-2);

    &::before {
        position: absolute;
        inset: 0;
        content: '';
        background-color: rgba(30, 56, 85, 0.5);
    }

    .confirm-container {
        position: relative;
        z-index: 1;
        display: flex;
        flex-direction: column;
        background: white;
        padding: 1.25rem;
        text-align: center;
        box-shadow: 0 0 var(--spacing-4) var(--color-gray-5);
        border-radius: var(--spacing-5);
        corner-shape: squircle;
        width: 100%;
        max-width: 100%;

        @media screen and (min-width: 768px) {
            box-shadow: 0 0 var(--spacing-4) var(--color-gray-5);
            border-radius: var(--spacing-5);
            corner-shape: squircle;
            width: 30rem;
            max-width: 30rem;
            padding: 2rem;
        }

        .confirm-icon {
            font-size: 4rem;
            color: var(--color-primary-4);
            margin-bottom: var(--spacing-2);

            &.confirm-danger {
                color: var(--color-red-5);
            }
        }

        .confirm-title {
            width: 100%;
            display: block;
            text-align: center;
            font-size: var(--font-size-4);
            font-weight: 600;
            margin-bottom: var(--spacing-0);
        }

        .confirm-subtitle {
            width: 100%;
            display: block;
            text-align: center;
            font-size: var(--font-size-2);
            color: var(--color-gray-6);
        }

        .confirm-actions {
            width: 100%;
            margin-top: var(--spacing-6);
            display: flex;
            flex-direction: column;
            align-items: stretch;
            justify-content: center;
            gap: var(--spacing-3);

            @media screen and (min-width: 768px) {
                flex-direction: row;
            }

            button {
                width: 100%;

                @media screen and (min-width: 768px) {
                    width: auto;
                }
            }
        }
    }
}

.confirm-backdrop-enter-active {
    transition: opacity 250ms ease-out;
}

.confirm-backdrop-leave-active {
    transition: opacity 50ms ease-in;
}

.confirm-backdrop-enter-from,
.confirm-backdrop-leave-to {
    opacity: 0;
}

.confirm-backdrop-enter-to,
.confirm-backdrop-leave-from {
    opacity: 1;
}

.confirm-container-enter-active {
    transition: transform 250ms cubic-bezier(0.16, 1, 0.3, 1),
    opacity 250ms ease-out;
}

.confirm-container-leave-active {
    transition: transform 50ms ease-in,
    opacity 50ms ease-in;
}

.confirm-container-enter-from,
.confirm-container-leave-to {
    opacity: 0;
}

.confirm-container-enter-to,
.confirm-container-leave-from {
    opacity: 1;
}

.confirm-container-enter-from,
.confirm-container-leave-to {
    transform: translateY(100%);
}

.confirm-container-enter-to,
.confirm-container-leave-from {
    transform: translateY(0);
}

@media screen and (min-width: 768px) {
    .confirm-container-enter-from,
    .confirm-container-leave-to {
        transform: scale(0.92) translateY(24px);
    }

    .confirm-container-enter-to,
    .confirm-container-leave-from {
        transform: scale(1) translateY(0);
    }
}
</style>
