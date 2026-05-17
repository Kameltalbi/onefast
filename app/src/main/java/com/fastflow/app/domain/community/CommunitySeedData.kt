package com.fastflow.app.domain.community

import com.fastflow.app.data.local.entity.CommunityPostEntity
import com.fastflow.app.domain.model.CommunityGroup
import com.fastflow.app.domain.model.CommunityPostType

object CommunitySeedData {
    const val SEED_AUTHOR_ID = "onefast_community"

    fun posts(): List<CommunityPostEntity> = listOf(
        seed(
            "OneFast",
            CommunityPostType.MOTIVATION,
            CommunityGroup.GENERAL,
            "Chaque heure de jeûne est un pas vers une meilleure version de vous-même. 💪"
        ),
        seed(
            "OneFast",
            CommunityPostType.MEAL_IDEA,
            CommunityGroup.GENERAL,
            "Après le jeûne : salade de quinoa, poulet grillé et légumes verts. Léger et nourrissant."
        ),
        seed(
            "OneFast",
            CommunityPostType.PROGRESS,
            CommunityGroup.GENERAL,
            "Membre anonyme : série de 7 jours · -2,3 % de poids en 3 semaines. Bravo !"
        ),
        seed(
            "OneFast",
            CommunityPostType.MOTIVATION,
            CommunityGroup.RAMADAN,
            "Le Ramadan nous rappelle la discipline et la gratitude. Un jour à la fois."
        ),
        seed(
            "OneFast",
            CommunityPostType.MEAL_IDEA,
            CommunityGroup.SUMMER,
            "Idée été : smoothie protéiné (banane, yaourt grec, amandes) en fenêtre alimentaire."
        )
    )

    private fun seed(
        author: String,
        type: CommunityPostType,
        group: CommunityGroup,
        content: String
    ): CommunityPostEntity = CommunityPostEntity(
        authorId = SEED_AUTHOR_ID,
        authorName = author,
        type = type.name,
        groupTag = group.name,
        content = content,
        createdAt = System.currentTimeMillis() - (postsOffset(type)),
        isSeedPost = true
    )

    private fun postsOffset(type: CommunityPostType): Long = when (type) {
        CommunityPostType.MOTIVATION -> 86_400_000L * 2
        CommunityPostType.MEAL_IDEA -> 86_400_000L
        CommunityPostType.PROGRESS -> 43_200_000L
    }
}
