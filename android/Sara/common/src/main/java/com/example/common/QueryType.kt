package com.example.common

enum class QueryType {
    ESSAY {
        override fun desc(): String ="이 사진으로 줄글 생성하기"
        override fun type(): Int = TYPE_1
        override fun str(): String ="작문"
    },
    POEM {
        override fun desc(): String ="이 사진으로 시 생성하기"
        override fun type(): Int = TYPE_2
        override fun str(): String ="시"
    },
    EVALUATION {
        override fun desc(): String ="이 사진으로 평가받기"
        override fun type(): Int = TYPE_3
        override fun str(): String ="평가"
    },
    FREE {
        override fun desc(): String ="텍스트 요청이 포함된 글 생성하기"
        override fun type(): Int = TYPE_4
        override fun str(): String ="자유글"
    };
    abstract fun desc(): String
    abstract fun type(): Int
    abstract fun str(): String

}

const val TYPE_1 = 1
const val TYPE_2 = 2
const val TYPE_3 = 3
const val TYPE_4 = 4