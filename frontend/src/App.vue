<script setup lang="ts">

import ToastOutlet from "./components/ToastOutlet.vue";
import {useRoute} from "vue-router";

const route = useRoute();
</script>

<template>
    <main>
        <router-view/>
    </main>
    <nav class="bottom-navigation" v-if="route.name !== 'login'">
        <div class="center-content-container">
            <ul>
                <li>
                    <router-link :to="{name: 'home'}"
                                 active-class="highlight">
                        <div class="icon-holder">
                            <i class="icon-home-outline"></i>
                        </div>
                        <span>Home</span>
                    </router-link>
                </li>
                <li>
                    <router-link :to="{name: 'search'}"
                                 active-class="highlight">
                        <div class="icon-holder">
                            <i class="icon-magnify"></i>
                        </div>
                        <span>Search</span>
                    </router-link>
                </li>
                <li>
                    <router-link :to="{name: 'create'}"
                                 active-class="highlight">
                        <div class="icon-holder">
                            <i class="icon-plus-box-outline"></i>
                        </div>
                        <span>Create</span>
                    </router-link>
                </li>
                <li>
                    <router-link :to="{name: 'notifications'}"
                                 active-class="highlight">
                        <div class="icon-holder">
                            <i class="icon-bell-outline"></i>
                        </div>
                        <span>Notifications</span>
                    </router-link>
                </li>
                <li>
                    <router-link :to="{name: 'profile'}"
                                 active-class="highlight">
                        <div class="icon-holder">
                            <i class="icon-account-outline"></i>
                        </div>
                        <span>Profile</span>
                    </router-link>
                </li>
            </ul>
        </div>
    </nav>
    <ToastOutlet/>
</template>

<style scoped>
.bottom-navigation {
    position: fixed;
    bottom: 0;
    left: 0;
    width: 100%;
    border-top: 1px solid var(--color-gray-2);

    ul {
        list-style: none;
        display: flex;
        align-items: center;
        justify-content: space-between;

        a {
            padding-block: var(--spacing-3);
            padding-inline: var(--spacing-2);
            display: flex;
            align-items: center;
            justify-content: center;
            flex-direction: column;
            gap: var(--spacing-1);
            text-decoration-line: none;
            color: var(--color-gray-3);

            i {
                font-size: var(--font-size-5);
            }

            &.highlight {
                color: var(--color-primary-4);
            }
        }
    }
}
</style>
