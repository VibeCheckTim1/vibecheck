<script setup lang="ts">
import PageHeaderComponent from "../../../components/PageHeaderComponent.vue";
import {useFormField} from "../../../composables/useFormField.ts";
import InputText from "../../../components/InputText.vue";

const searchKeyword = useFormField<string>(null);
</script>

<template>
    <PageHeaderComponent>
        <span>Search</span>
    </PageHeaderComponent>
    <div class="center-content-container">
        <div class="search-input-holder">
            <InputText :control="searchKeyword"
                       placeholder="Search...">
                <template #prefix>
                    <i class="icon-magnify"></i>
                </template>
            </InputText>
        </div>
        <section>
            <div class="section-title">
                <i class="icon-trending-up"></i>
                <span>Trending now</span>
            </div>
            <ul class="trending-list">
                <li>
                    <div class="trending-icon-holder">
                        <i class="icon-playlist-music"></i>
                    </div>
                    <div class="trending-details">
                        <span class="trending-title">Indie Summer</span>
                        <span class="trending-subtitle">playlist · 12.5K listeners</span>
                    </div>
                </li>
                <li>
                    <div class="trending-icon-holder">
                        <i class="icon-microphone"></i>
                    </div>
                    <div class="trending-details">
                        <span class="trending-title">Billie Eilish</span>
                        <span class="trending-subtitle">artist · 89.2M listeners</span>
                    </div>
                </li>
                <li>
                    <div class="trending-icon-holder">
                        <i class="icon-playlist-music"></i>
                    </div>
                    <div class="trending-details">
                        <span class="trending-title">Midnight Drive</span>
                        <span class="trending-subtitle">playlist · 8.3K listeners</span>
                    </div>
                </li>
            </ul>
        </section>
        <section>
            <div class="section-title">
                <i class="icon-play-circle-outline"></i>
                <span>Browse by Genre</span>
            </div>
            <div class="genres-container">
                <div class="genre-card hiphop">Hip Hop</div>
                <div class="genre-card rock">Rock</div>
                <div class="genre-card electronic">Electronic</div>
                <div class="genre-card jazz">Jazz</div>
                <div class="genre-card pop">Pop</div>
                <div class="genre-card rnb">R&B</div>
            </div>
        </section>
    </div>
</template>

<style scoped>
.search-input-holder {
    padding-top: var(--spacing-4);
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


.trending-list {
    list-style: none;
    display: flex;
    flex-direction: column;
    gap: var(--spacing-6);

    li {
        display: flex;
        align-items: center;
        justify-content: flex-start;
        gap: var(--spacing-3);
        cursor: pointer;

        .trending-icon-holder {
            background: var(--color-primary-4);
            color: white;
            font-size: var(--font-size-6);
            padding: var(--spacing-2);
            display: flex;
            align-items: center;
            justify-content: center;
            border-radius: var(--border-radius-5);
        }

        .trending-details {
            display: flex;
            flex-direction: column;
            align-items: flex-start;

            .trending-title {
                font-weight: bold;
                line-height: 1;
            }

            .trending-subtitle {
                font-size: var(--font-size-1);
                color: var(--color-gray-5);
            }
        }
    }
}

.genres-container {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: var(--spacing-2);

    .genre-card {
        padding: var(--spacing-4);
        border-radius: var(--border-radius-5);
        color: white;
        font-weight: bold;
        font-size: var(--font-size-2);
        cursor: pointer;

        &.hiphop {
            background: #ff2d2d;
        }

        &.rock {
            background: #3b7ddd;
        }

        &.electronic {
            background: #7b2ff7;
        }

        &.jazz {
            background: #ff9800;
        }

        &.pop {
            background: #ff2e92;
        }

        &.rnb {
            background: #00c853;
        }
    }
}
</style>
