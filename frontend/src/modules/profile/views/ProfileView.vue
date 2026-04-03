<script setup lang="ts">
import {useState} from "../../../composables/useState.ts";
import {ref} from "vue";
import type {Playlist} from "../../../entities/playlist.ts";
import PageHeaderComponent from "../../../components/PageHeaderComponent.vue";

const {currentUser} = useState();

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
</script>

<template>
    <PageHeaderComponent>
        <span>Profile</span>
    </PageHeaderComponent>
    <div class="center-content-container">
        <div class="profile-container" v-if="currentUser">
            <div class="user-avatar-holder">

            </div>
            <div class="user-full-name">{{ currentUser.email }}</div>
            <div class="user-username">{{ currentUser.username }}</div>
            <p class="user-description">Music enthusiast 🎵 Always discovering new sounds · Indie & Electronic lover</p>
            <a href="#" class="primary-button">Edit profile</a>
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
        </div>
    </div>
</template>

<style scoped>
.profile-container {
    padding-block: var(--spacing-10);
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
}
</style>
