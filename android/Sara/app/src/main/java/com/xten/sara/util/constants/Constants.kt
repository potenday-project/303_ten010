package com.xten.sara.util

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-03-28
 * @desc const val file
 */

const val TAG = "[확인]"

const val SARA_PREFS = "sara_prefs"

const val MESSAGE_PERMISSION_CAMERA = "카메라 권한을 허용해 주세요"
const val MESSAGE_PERMISSION_ACCESS_FILE = "파일 접근 권한을 허용해 주세요"

const val MESSAGE_WARNING_EDIT = "텍스트를 입력해주세요."
const val MESSAGE_WARNING_ERROR = "로그인을 할 수 없습니다."

const val MESSAGE_RESULT_SAVE_SUCCESS = "컬렉션에 공유하였습니다."
const val MESSAGE_RESULT_SAVE_FAIL= "저장 할 수 없습니다."

const val MESSAGE_RESULT_DELETE_SUCCESS = "컬렉션에서 삭제되었습니다."
const val MESSAGE_RESULT_DELETE_FAIL= "삭제 할 수 없습니다."

const val MESSAGE_RESULT_LOGOUT_SUCCESS = "로그아웃 하였습니다."
const val MESSAGE_RESULT_LOGOUT_FAIL = "로그아웃 할 수 없습니다."

const val DEFAULT_ = 0
const val RANDOM_SIZE = 2

const val TYPE_1 = 0
const val TYPE_2 = 1
const val TYPE_3 = 2
const val TYPE_4 = 3

enum class State {
    NONE, SUCCESS, FAIL
}

const val LABEL_SPLASH_ = "fragment_splash"
const val LABEL_LOGIN_ = "fragment_login"
const val LABEL_IMAGE_UPLOAD_ = "fragment_image_upload"
const val LABEL_IMAGE_RESULT_ = "fragment_image_result"

const val GRID_COL_TYPE_1 = 2

const val GALLERY = "gallery"

enum class QueryType {
    ESSAY {
        override fun desc(): String ="이 사진으로 줄글 생성하기"
        override fun type(): Int = 1
    },
    POEM {
        override fun desc(): String ="이 사진으로 시 생성하기"
        override fun type(): Int = 2
    },
    EVALUATION {
        override fun desc(): String ="이 사진으로 평가받기"
        override fun type(): Int = 3
    },
    FREE {
        override fun desc(): String ="텍스트 요청이 포함된 글 생성하기"
        override fun type(): Int = 4
    };
    abstract fun desc() : String
    abstract fun type() : Int
}