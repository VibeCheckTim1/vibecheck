<script setup lang="ts">
import {watch} from 'vue';
import {useState} from "./composables/useState.ts";
import {useFollowingService} from "./modules/followers/composables/useFollowingService.ts";
import DialogOutlet from "./components/DialogOutlet.vue";
import ToastOutlet from "./components/ToastOutlet.vue";
import ConfirmOutlet from "./components/ConfirmOutlet.vue";
import {useFollowingStore} from "./modules/followers/stores/useFollowingStore.ts";

const { currentUser } = useState();

const followingStore = useFollowingStore();
const { getAllFollowRequests } = useFollowingService();

watch(() => currentUser.value, async (user) => {
    if (!user) return;
    await getAllFollowRequests(true);
},
{
    immediate: true
});
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
                            <span class="notifications-badge" v-if="followingStore.followRequests.length > 0">{{followingStore.followRequests.length}}</span>
                            <i class="icon-bell-outline"></i>
                        </div>
                        <span>Notifications</span>
                    </router-link>
                </li>
                <li>
                    <router-link :to="{name: 'account', params: {userId: String(currentUser.idUser)}}"
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

            .icon-holder {
                position: relative;

                .notifications-badge {
                    position: absolute;
                    top: -5px;
                    right: -5px;
                    background-color: var(--color-red-5);
                    color: white;
                    font-size: var(--font-size-1);
                    width: 16px;
                    height: 16px;
                    border-radius: 50%;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                }
            }
        }
    }
}
</style>
