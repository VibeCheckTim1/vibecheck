<script setup lang="ts">
import {useState} from "../../../composables/useState.ts";
import {computed, onMounted, ref, watch} from "vue";
import type {Playlist} from "../../../entities/playlist.ts";
import PageHeaderComponent from "../../../components/PageHeaderComponent.vue";
import {useRoute} from "vue-router";
import {ApiError} from "../../../composables/useHttpClient.ts";
import router from "../../../router";
import {useToast} from "../../../composables/useToast.ts";
import {useProfileService} from "../composables/useProfileService.ts";
import type {User} from "../../../entities/user.ts";

const {currentUser} = useState();
const route = useRoute();

const isOwnProfile = ref(true);
const viewedUser = ref<User | null>(null);
const {showError} = useToast();

async function logout() {
    if (currentUser.value) {
        const {logoutAction} = useProfileService(currentUser.value.idUser);
        await logoutAction();
        window.location.href = "/";
    }
}

const isPrivateProfile = computed(() => {
    return isOwnProfile.value === false && viewedUser.value?.isPrivate;
});

const playlists = ref<Playlist[]>([
    {
        id: 1,
        name: "Top Hits 2025",
        songCount: 42,
    },
    {
        id: 2,
        name: "Chill Vibes",
        songCount: 18,
    },
    {
        id: 3,
        name: "Workout Mix",
        songCount: 27,
    },
    {
        id: 4,
        name: "Old School Classics",
        songCount: 35,
    },
    {
        id: 5,
        name: "Focus & Study",
        songCount: 22,
    },
]);

const loadUser = async (userIdRoute: number) => {
    if (!currentUser.value) {
        return;
    }

    isOwnProfile.value = currentUser.value.idUser === userIdRoute;
    if (isOwnProfile.value) {
        viewedUser.value = currentUser.value;
    }
    else {
        try {
            const {getUser} = useProfileService(userIdRoute);
            viewedUser.value = await getUser();
        }
        catch (error) {
            await router.push({name: "home"});
            if (error instanceof ApiError) {
                showError(error.message);
            }
        }
    }
};

watch(() => route.params.userId, async (newUserId) => {
    if (!newUserId) return;

    await loadUser(Number(newUserId));
}, {
    immediate: true,
});

onMounted(() => {
    const userIdRoute = Number(route.params.userId);
    loadUser(userIdRoute);
});
</script>

<template>
    <PageHeaderComponent>
        <span>Profile</span>
        <template #actions v-if="isOwnProfile">
            <button class="danger-button" type="button" @click="logout">Logout</button>
        </template>
    </PageHeaderComponent>
    <div class="center-content-container">
        <div class="profile-container" v-if="viewedUser">
            <div class="user-avatar-holder">
                <img v-if="viewedUser.avatarUrl" :src="viewedUser.avatarUrl" alt="Avatar">
            </div>
            <div class="user-full-name">{{ viewedUser.email }}</div>
            <div class="user-username">{{ viewedUser.username }}</div>
            <p class="user-description" v-if="viewedUser.bio">{{ viewedUser.bio }}</p>
            <router-link :to="{name: 'updateProfile'}" class="primary-button" v-if="isOwnProfile">Edit profile</router-link>
            <template v-if="isPrivateProfile === false">
                <div class="profile-info">
                    <ul>
                        <li>
                            <span class="value">1.2K</span>
                            <span class="label">Followers</span>
                        </li>
                        <li>
                            <span class="value">856</span>
                            <span class="label">Following</span>
                        </li>
                        <li>
                            <span class="value">24</span>
                            <span class="label">Playlists</span>
                        </li>
                    </ul>
                </div>

                <section>
                    <div class="section-title">
                        <i class="icon-chart-bar"></i>
                        <span>Top genres</span>
                    </div>
                    <ul class="genres-list">
                        <li>Indie rock</li>
                        <li>Electronic</li>
                        <li>R&B</li>
                        <li>Jazz</li>
                        <li>Hip Hop</li>
                    </ul>
                </section>

                <section>
                    <div class="section-title">
                        <i class="icon-playlist-music"></i>
                        <span>My playlists</span>
                    </div>
                    <div class="playlists-container">
                        <div class="playlist-card" v-for="playlist in playlists" :key="playlist.id">
                            <div class="playlist-cover">

                            </div>
                            <span class="playlist-name">{{ playlist.name }}</span>
                            <span class="playlist-song-count">{{ playlist.songCount }} songs</span>
                        </div>
                    </div>
                </section>
            </template>
            <div class="private-profile-container">
                <div class="icon-holder">
                    <i class="icon-shield-lock-outline"></i>
                </div>
                <p class="private-profile-message">This account is private!</p>
            </div>
        </div>
    </div>
</template>

<style scoped>
.profile-container {
    padding-block: var(--spacing-5);
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
    width: 100%;

    .user-avatar-holder {
        width: 90px;
        height: 90px;
        border-radius: 50%;
        background-color: var(--color-primary-4);
        display: flex;
        align-items: center;
        justify-content: center;
        margin-bottom: var(--spacing-2);

        img {
            width: 90px;
            height: 90px;
            border-radius: 50%;
            border: 2px solid var(--color-primary-4);
        }
    }

    .user-full-name {
        width: 100%;
        text-align: center;
        font-weight: bold;
        margin-top: var(--spacing-6);
    }

    .user-username {
        width: 100%;
        text-align: center;
        font-size: var(--font-size-1);
        color: var(--color-gray-2);
        margin-bottom: var(--spacing-2)
    }

    .user-description {
        width: 100%;
        text-align: center;
        font-size: var(--font-size-1);
        padding-inline: var(--spacing-4);
        margin-bottom: var(--spacing-6);
    }

    .profile-info {
        margin-top: var(--spacing-8);
        width: 100%;
        background-color: var(--color-gray-0);
        border-radius: var(--border-radius-6);
        padding: var(--spacing-2);

        ul {
            list-style: none;
            display: flex;
            align-items: center;
            justify-content: space-evenly;

            li {
                display: flex;
                align-items: center;
                justify-content: center;
                flex-direction: column;
                padding: var(--spacing-2);

                .value {
                    font-size: var(--font-size-5);
                    font-weight: bold;
                }

                .label {
                    font-size: var(--font-size-1);
                }
            }
        }
    }

    section {
        margin-top: var(--spacing-8);
        width: 100%;

        .section-title {
            display: flex;
            align-items: center;
            justify-content: flex-start;
            gap: var(--spacing-2);
            margin-bottom: var(--spacing-2);

            i {
                color: var(--color-primary-4);
                font-size: var(--font-size-5);
            }

            span {
                font-weight: bold;
            }
        }
    }

    .genres-list {
        list-style: none;
        display: flex;
        align-items: center;
        justify-content: flex-start;
        gap: var(--spacing-2);

        li {
            background-color: var(--color-gray-0);
            padding-inline: var(--spacing-3);
            padding-block: var(--spacing-1);
            font-size: var(--font-size-1);
            border-radius: var(--border-radius-6);
        }
    }

    .playlists-container {
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        gap: var(--spacing-3);

        @media screen and (max-width: 768px) {
            grid-template-columns: 1fr;

        }

        .playlist-card {
            background-color: var(--color-gray-0);
            padding: var(--spacing-3);
            border-radius: var(--border-radius-7);

            .playlist-cover {
                width: 100%;
                aspect-ratio: 1 / 1;
                background-color: white;
                border-radius: var(--border-radius-4);
                margin-bottom: var(--spacing-2);
            }

            .playlist-name {
                font-weight: bold;
                display: block;
            }

            .playlist-song-count {
                font-size: var(--font-size-1);
                color: var(--color-gray-4);
                display: block;
            }
        }
    }

    .private-profile-container {
        background-color: var(--color-gray-0);
        border-radius: var(--border-radius-6);
        padding-block: 10%;
        padding-inline: var(--spacing-3);
        width: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
        flex-direction: column;

        .icon-holder {
            font-size: var(--font-size-8);
            color: var(--color-primary-4);
            display: flex;
            align-items: center;
            justify-content: center;
            width: 60px;
            height: 60px;
        }

        .private-profile-message {
            text-align: center;
            font-size: var(--font-size-3);
            color: var(--color-gray-6);
            font-weight: bold;
        }
    }
}
</style>
