<script setup lang="ts">
import { useState } from "../../../composables/useState.ts";
import { computed, onMounted, ref, watch } from "vue";
import type { Playlist } from "../../../entities/playlist.ts";
import PageHeaderComponent from "../../../components/PageHeaderComponent.vue";
import { useRoute } from "vue-router";
import { ApiError } from "../../../composables/useHttpClient.ts";
import router from "../../../router";
import { useToast } from "../../../composables/useToast.ts";
import type { User } from "../../../entities/user.ts";
import { type FollowStatsResponse, useFollowingService } from "../../followers/composables/useFollowingService.ts";
import { useConfirm } from "../../../composables/useConfirm.ts";
import {useUserService} from "../../../composables/useUserService.ts";
import {useSecurityService} from "../../../composables/useSecurityService.ts";

const { currentUser } = useState();
const route = useRoute();

const isOwnProfile = ref(true);
const viewedUser = ref<User | null>(null);
const { showError } = useToast();

const followResult = ref<'FOLLOW' | 'FOLLOWING' | 'PENDING'>('FOLLOW');
const { openConfirm } = useConfirm();

const stats = ref<FollowStatsResponse | null>(null);

async function logout() {
    if (currentUser.value) {
        const { logoutAction } = useSecurityService();
        await logoutAction();
        window.location.href = "/";
    }
}

const isPrivateProfile = computed(() => {
    return isOwnProfile.value === false && viewedUser.value?.isPrivate;
});



async function follow() {
    if (!currentUser.value || !viewedUser.value) return;

    try {
        const { createFollowRequest } = useFollowingService(viewedUser.value?.idUser);
        const followApiResult = await createFollowRequest({
            receiverId: viewedUser.value?.idUser
        });
        //followResult.value = (await createFollowRequest({  receiverId: viewedUser.value?.idUser })).result;
        console.log(followResult.value);


        if (followApiResult.result === 'FOLLOWING') {
            followResult.value = 'FOLLOWING';
        }
        else if (followApiResult.result === 'PENDING') {
            followResult.value = 'PENDING';
        }

    }
    catch (error) {
        if (error instanceof ApiError) {
            showError(error.message);
        }
    }
}


async function cancelFollowRequest() {
    if (!currentUser.value || !viewedUser.value) return;

    const confirmed = await openConfirm({
        isDanger: true,
        title: "Cancel follow request",
        subtitle: "Are you sure you want to cancel follow request for this user?",
        acceptMessage: "Yes, cancel request",
    });

    if (confirmed) {
        try {
            const { cancelFollowRequest } = useFollowingService(viewedUser.value.idUser);
            await cancelFollowRequest();

            followResult.value = 'FOLLOW';
        }
        catch (error) {
            if (error instanceof ApiError) {
                showError(error.message);
            }
        }
    }
}

async function unfollow() {
    if (!currentUser.value || !viewedUser.value) return;

    const confirmed = await openConfirm({
        isDanger: true,
        title: "Unfollow user",
        subtitle: "Are you sure you want to unfollow this user?",
        acceptMessage: "Yes, unfollow user",
    });

    if (confirmed) {
        try {
            const { unfollow } = useFollowingService(viewedUser.value.idUser);
            await unfollow();

            followResult.value = 'FOLLOW';
        }
        catch (error) {
            if (error instanceof ApiError) {
                showError(error.message);
            }
        }
    }
}

const followButtonText = computed(() => {
    if (followResult.value === 'FOLLOWING') return 'Following';
    if (followResult.value === 'PENDING') return 'Pending';
    return 'Follow';
});

const handleButtonClass = computed(() => {
    if (followResult.value === 'FOLLOWING') return 'follow-btn--following'
    if (followResult.value === 'PENDING') return 'follow-btn--pending';
    return 'follow-btn--follow';
});

async function handleFollowClick() {
    if (followResult.value === 'FOLLOWING') { //unfollow
        await unfollow();
        return;
    }
    if (followResult.value === 'PENDING') { //cancel follow req
        await cancelFollowRequest();
        return;
    }
    await follow(); //follow
}

async function getStats() {
    if (!viewedUser.value) return;

    const { getFollowStats } = useFollowingService();
    stats.value = await getFollowStats();

}


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
            const { getUser } = useUserService();
            viewedUser.value = await getUser(userIdRoute);
        }
        catch (error) {
            await router.push({ name: "home" });
            if (error instanceof ApiError) {
                showError(error.message);
            }
        }
    }
};

async function getFollowStatus() {
    if (!viewedUser.value || isOwnProfile.value) return;
    const { getFollowStatus } = useFollowingService(viewedUser.value?.idUser);

    followResult.value = (await getFollowStatus()).result;
}

watch(() => route.params.userId, async (newUserId) => {
    if (!newUserId) return;

    await loadUser(Number(newUserId));
}, {
    immediate: true,
});

onMounted(async () => {
    const userIdRoute = Number(route.params.userId);
    await loadUser(userIdRoute);

    await getFollowStatus();
    await getStats();

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
            <div data-testid="profile-username" class="user-username">{{ viewedUser.username }}</div>
            <p class="user-description" v-if="viewedUser.bio">{{ viewedUser.bio }}</p>

            <button data-testid="follow-button" :class="['follow-btn', handleButtonClass]" v-if="!isOwnProfile" @click="handleFollowClick">{{
                    followButtonText
                }}</button>

            <router-link :to="{ name: 'updateProfile' }" class="primary-button" v-if="isOwnProfile">Edit
                profile</router-link>
            <template v-if="isPrivateProfile === false">
                <div class="profile-info">
                    <ul>
                        <li>
                            <span class="value">{{ stats?.followersCount }}</span>
                            <span class="label">Followers</span>
                        </li>
                        <li>
                            <span class="value">{{ stats?.followingCount }}</span>
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
            <div v-if="viewedUser.isPrivate" class="private-profile-container">
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

    .follow-btn {
        min-width: 130px;
        height: 44px;
        padding: 0 20px;
        border: none;
        border-radius: 12px;
        font-size: 15px;
        font-weight: 600;
        cursor: pointer;
        transition: all 0.2s ease;
        letter-spacing: 0.2px;

        display: inline-flex;
        align-items: center;
        justify-content: center;
        gap: 6px;
    }

    /* FOLLOW */
    .follow-btn--follow {
        background: linear-gradient(135deg, #7c3aed, #9333ea);
        color: #fff;
        box-shadow: 0 6px 18px rgba(124, 58, 237, 0.35);
    }

    .follow-btn--follow:hover {
        transform: translateY(-2px);
        box-shadow: 0 10px 24px rgba(124, 58, 237, 0.45);
    }

    .follow-btn--follow:active {
        transform: scale(0.97);
    }


    /* FOLLOWING */
    .follow-btn--following {
        background: linear-gradient(135deg, #ede9fe, #ddd6fe);
        color: #5b21b6;
        border: 1px solid #c4b5fd;
    }

    .follow-btn--following::before {
        content: "✓";
        font-size: 14px;
    }

    /* hover = unfollow hint */
    .follow-btn--following:hover {
        background: #ef4444;
        color: #fff;
        border-color: #ef4444;
    }


    /* PENDING */
    .follow-btn--pending {
        background: rgba(245, 158, 11, 0.14);
        color: #f59e0b;
        border: 1px solid rgba(245, 158, 11, 0.3);
    }

    .follow-btn--pending:hover {
        background: rgba(245, 158, 11, 0.22);
        border-color: rgba(245, 158, 11, 0.45);
    }


    /* DISABLED */
    .follow-btn:disabled {
        opacity: 0.6;
        cursor: not-allowed;
        transform: none;
        box-shadow: none;
    }


}
</style>
