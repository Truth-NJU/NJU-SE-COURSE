<template>
  <v-card :color="courseColor" dark width="380" height="260" class="ma-4 pa-2">
    <v-card-title class="headline">
      {{ courseName }}
      <v-chip
          small
          class="ml-4"
          v-show="status === 1 || status === 0|| status===2||status===3"
          :color="chipColor[status]"
      >
        {{ chip[status] }}
      </v-chip>
    </v-card-title>

    <v-card-text class="text">
      【{{ type }}课程】
      {{ text }}
    </v-card-text>

    <v-card-actions>
      <v-btn text v-show="status === 0 || status === 1 || status===2||status===3" @click="handleStudy">学习课程</v-btn>
      <v-btn text v-show="status === -1 || !hasLogin" @click="handlePeek">浏览课程</v-btn>
      <v-btn text v-show="status === -1 || (status === 0 && !bought)" @click="buyCourse">
        {{ cost === 0 ? "免费购买" : "购买课程" }}
      </v-btn>
      <v-btn text v-show="status === -1 || status === 2" @click="rentCourse"
      >{{ rented === true ? "续租" + rentEndTime : "租用课程" }}
      </v-btn>
    </v-card-actions>
    <v-row justify="end" class="pr-5">
      <v-btn
          class="mx-2 align-self-center"
          fab
          dark
          small
          :color="!liked ? 'red' : 'black'"
          @click="handleLike"
      >
        <v-icon v-if="liked" dark>
          mdi-thumb-down
        </v-icon>
        <v-icon dark v-else>
          mdi-heart
        </v-icon>
      </v-btn>
      <span class="align-self-center">点赞： {{ courseLikes }}</span>
    </v-row>
  </v-card>
</template>

<script lang="ts">
import Vue from "vue";

export default Vue.extend({
  name: "CourseItem",
  props: {
    courseName: {
      type: String,
      default: "SubjectName"
    },
    courseId: {
      type: Number,
      default: 0
    },
    description: {
      type: String,
      default: "课程简介"
    },
    type: {
      type: String,
      default: "初级"
    },
    cost: {
      type: Number,
      default: 0
    },
    bought: {
      type: Boolean,
      default: false
    },
    rented: {
      type: Boolean,
      default: false
    },
    rentEndTime: {
      type: Date,
      default: new Date()
    },
    manageable: {
      type: Boolean,
      default: false
    },
    hasLogin: {
      type: Boolean,
      default: true
    },
    courseColor: {
      type: String,
      default: "#BCAAA4"
    },
    courseLikes: {
      type: Number,
      default: 0
    },
    liked: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      chip: ["免费", "已购", "已租", "vip"],
      chipColor: ["success", "primary"]
    };
  },
  methods: {
    rentCourse() {
      this.$emit("rent-course", this.courseId, this.courseName, this.cost, this.rented, this.rentEndTime)
    },
    buyCourse() {
      this.$emit("buy-course", this.courseId, this.courseName, this.cost);
    },
    handleStudy() {
      if (this.hasLogin) {
        this.$router.push(`/student/course/${this.courseId}`);
      } else {
        this.$emit("buy-course", this.courseId, this.courseName, this.cost);
      }
    },

    handlePeek() {
      this.$router.push(`/student/peek/${this.courseId}`);
    },
    handleLike() {
      this.$emit("set-like", this.courseId);
    }
  },
  computed: {
    text: function () {
      return this.description.length < 60
          ? this.description
          : this.description.substring(0, 60) + "...";
    },

    // 0 免费  1 已购   2已租
    status: function () {
      if (this.cost === 0) {
        return 0;
      } else if (this.bought) {
        return 1;
      } else if (this.rented) {
        return 2;
      } else if (localStorage.getItem("userIsVip") === "1") {
        return 3;
      }
      return -1;
    }
  }
});
</script>

<style scoped>
.text {
  height: 85px;
  overflow: hidden;
}
</style>
