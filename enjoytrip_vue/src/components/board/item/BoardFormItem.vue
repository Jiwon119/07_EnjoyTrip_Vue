<script setup>
import { registArticle, modifyArticle, detailArticle } from "@/api/board";
import { ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";

const router = useRouter();
const route = useRoute();

const props = defineProps({ type: String });

const isUseId = ref(false);

const article = ref({
  articleNo: 0,
  subject: "1234",
  content: "123",
  userId: "ssafy",
  userName: "",
  hit: 0,
  registerTime: ""
});

const upfile = ref();
const image = ref();

if (props.type === "modify") {
  let { articleno } = route.params;
  console.log(articleno + "번글 얻어와서 수정할거야");
  // API 호출
  isUseId.value = true;

  detailArticle(articleno, ({ data }) => {
    console.log(data);
    article.value = data.article;
    console.log(data)
  },
    (error) => {
      console.log(error);
    })
  console.log(article)
}

const subjectErrMsg = ref("");
const contentErrMsg = ref("");
watch(
  () => article.value.subject,
  (value) => {
    let len = value.length;
    if (len == 0 || len > 30) {
      subjectErrMsg.value = "제목을 확인해 주세요!!!";
    } else subjectErrMsg.value = "";
  },
  { immediate: true }
);
watch(
  () => article.value.content,
  (value) => {
    let len = value.length;
    if (len == 0 || len > 500) {
      contentErrMsg.value = "내용을 확인해 주세요!!!";
    } else contentErrMsg.value = "";
  },
  { immediate: true }
);

function onSubmit() {

  if (subjectErrMsg.value) {
    alert(subjectErrMsg.value);
  } else if (contentErrMsg.value) {
    alert(contentErrMsg.value);
  } else {
    props.type === "regist" ? writeArticle() : updateArticle();
  }
}


function writeArticle() {
  console.log("글등록하자!!", article.value);
  registArticle(article.value, upfile, (response) => {
    console.log(response);
  },
    (error) => {
      console.log(error);
    })
  moveList();
}

function updateArticle() {
  console.log(article.value.articleNo + "번글 수정하자!!", article.value);
  // API 호출
  modifyArticle(article.value, () => {
    console.log(article.value);
  },
    (error) => {
      console.log(error);
    })
  moveList();
}

function moveList() {
  router.push({ name: "article-list" });
}

function upload() {
     
      console.log(this)
      // this.imageUploaded = URL.createObjectURL(this.image)
    }
  

</script>

<template>
  <form @submit.prevent="onSubmit">
    <div class="mb-3">
      <label for="userid" class="form-label">작성자 ID : </label>
      <input type="text" class="form-control" v-model="article.userId" :disabled="isUseId" placeholder="작성자ID..." />
    </div>
    <div class="mb-3">
      <label for="subject" class="form-label">제목 : </label>
      <input type="text" class="form-control" v-model="article.subject" placeholder="제목..." />
    </div>
    <div class="mb-3">
      <label for="content" class="form-label">내용 : </label>
      <textarea class="form-control" v-model="article.content" rows="10"></textarea>
    </div>

    <div class="mb-3">
				<label for="upfile" class="form-label">파일:</label>
				<input type="file" class="form-control border" @change="upload(this)" id="upfile" name="upfile" multiple="multiple">
			</div>

    <div class="col-auto text-center">
      <button type="submit" class="btn btn-outline-primary mb-3" v-if="type === 'regist'" @click="writeArticle">
        글작성
      </button>
      <button type="submit" class="btn btn-outline-success mb-3" v-else @click="updateArticle">글수정</button>
      <button type="button" class="btn btn-outline-danger mb-3 ms-1" @click="moveList">
        목록으로이동...
      </button>
    </div>
  </form>
</template>

<style scoped></style>
