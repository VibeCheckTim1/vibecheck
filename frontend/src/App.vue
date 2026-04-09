<script setup lang="ts">

import ToastOutlet from "./components/ToastOutlet.vue";
import {useState} from "./composables/useState.ts";
import DialogOutlet from "./components/DialogOutlet.vue";
import ConfirmOutlet from "./components/ConfirmOutlet.vue";

const {currentUser} = useState();
</script>

<template>
    <main>
        <router-view/>
    </main>
    <nav class="bottom-navigation" v-if="currentUser">
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
                    <router-link :to="{name: 'profile', params: {userId: String(currentUser.idUser)}}"
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
    <DialogOutlet/>
    <ToastOutlet/>
    <ConfirmOutlet/>
</template>

<style scoped>
main {
    padding-bottom: 100px;
}

.bottom-navigation {
    position: fixed;
    bottom: 0;
    left: 0;
    width: 100%;
    border-top: 1px solid var(--color-gray-2);
    background-color: white;

    ul {
        list-style: none;
        display: flex;
        align-items: center;
        justify-content: space-evenly;
        gap: var(--spacing-2);

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
