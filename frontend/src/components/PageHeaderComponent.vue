<script setup lang="ts">
import {useRouter} from "vue-router";
import {computed} from "vue";
import {getPreviousRouteName} from "../router";

const router = useRouter();

const canGoBack = computed(() => {
    const previous = getPreviousRouteName();
    return previous !== null && previous !== "login";
});

const goBack = () => {
    if (canGoBack.value) {
        router.back();
    }
};
</script>

<template>
    <header>
        <div class="center-content-container page-header">
            <div class="header-title">
                <button class="return-back-button" v-if="canGoBack" @click="goBack()">
                    <i class="icon-arrow-left"></i>
                </button>
                <slot></slot>
            </div>
            <div class="header-action">
                <slot name="actions"></slot>
            </div>
        </div>
    </header>
</template>

<style scoped>
header {
    position: sticky;
    top: 0;
    left: 0;
    width: 100%;
    border-bottom: 1px solid var(--color-gray-2);
    background-color: white;

    .center-content-container {
        height: 60px;
    }

    .page-header {
        display: flex;
        align-items: stretch;
        justify-content: space-between;
        gap: var(--spacing-2);
        font-weight: bold;
        padding-block: var(--spacing-2);

        .header-title {
            font-weight: bold;
            font-size: var(--font-size-4);
            display: flex;
            align-items: center;
            justify-content: flex-end;
            gap: var(--spacing-2);
            color: var(--color-gray-8);

            .return-back-button {
                width: 30px;
                height: 30px;
                background-color: white;
                outline: none;
                border: none;
                cursor: pointer;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: var(--font-size-3);
                color: var(--color-gray-6);
            }
        }

        .header-action {
            display: flex;
            align-items: center;
            justify-content: flex-end;
            gap: var(--spacing-2);
        }
    }
}
</style>
