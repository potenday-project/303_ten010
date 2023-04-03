package com.xten.sara.util.constants

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-03-28
 * @desc const val file
 */

const val TAG = "[확인]"

const val SARA_PREFS = "sara_prefs"

const val APP_NAME = "Sara"


const val SURVEY_URL = "https://forms.gle/WoHSYcZk5LSDawHu5"

const val MESSAGE_PERMISSION_CAMERA = "카메라 권한을 허용해 주세요"
const val MESSAGE_PERMISSION_ACCESS_FILE = "파일 접근 권한을 허용해 주세요"

const val MESSAGE_WARNING_EDIT = "텍스트를 입력해주세요."
const val MESSAGE_LOGIN_SUCCESS = "Sara에 오신 걸 환영합니다!"
const val MESSAGE_WARNING_ERROR = "로그인을 할 수 없습니다."

const val MESSAGE_RESULT_UPLOAD_FAIL = "사진을 업로드 할 수 없습니다."
const val MESSAGE_RESULT_AI_FAIL = "요청에 실패하였습니다."
const val MESSAGE_CANCEL = "요청을 취소했습니다."

const val MESSAGE_RESULT_SAVE_SUCCESS = "컬렉션에 공유하였습니다."
const val MESSAGE_RESULT_SAVE_FAIL= "저장 할 수 없습니다."

const val MESSAGE_RESULT_DELETE_SUCCESS = "컬렉션에서 삭제되었습니다."
const val MESSAGE_RESULT_DELETE_FAIL= "삭제 할 수 없습니다."

const val MESSAGE_RESULT_SEARCH_FAIL = "검색결과가 없습니다"

const val MESSAGE_RESULT_LOGOUT_SUCCESS = "로그아웃 하였습니다."
const val MESSAGE_RESULT_LOGOUT_FAIL = "로그아웃 할 수 없습니다."

const val MESSAGE_TEXT_COPY = "텍스트가 복사되었습니다."

const val DEFAULT_ = 0
const val RANDOM_SIZE = 2

const val TYPE_1 = 1
const val TYPE_2 = 2
const val TYPE_3 = 3
const val TYPE_4 = 4

enum class State {
    NONE, SUCCESS, FAIL, ING
}

const val LABEL_HOME_ = "fragment_home"
const val LABEL_GALLERY_ = "fragment_gallery"
const val LABEL_MY_ = "fragment_my"
const val LABEL_IMAGE_RESULT_ = "fragment_image_result"

const val GRID_COL_TYPE_1 = 2

const val TYPE_ALBUM = 0
const val TYPE_LIST = 1
const val DEFAULT_POSITION = 0

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

const val TEXT_FIELD_ERROR_MESSAGE = "최대 30글자를 넘을 수 없습니다."
const val TEXT_FIELD_ERROR_MESSAGE_TITLE = "최대 20글자를 넘을 수 없습니다."
const val MAX_TEXT_LENGTH = 30
const val MAX_TEXT_TITLE_LENGTH = 20

const val SHARE_TITLE_TEXT = "친구에게 공유하기"