<template>
  <inner-link
    v-for="(item, index) in tagsViewStore.iframeViews"
    :key="item.path"
    :iframeId="'iframe' + index"
    v-show="route.path === item.path"
    :src="iframeUrl(item.meta.link, item.query)"
  ></inner-link>
</template>

<script setup lang="ts">
import InnerLink from "../InnerLink/index";
import useTagsViewStore from "@/store/modules/tagsView";
import {useRoute} from "vue-router";

const route = useRoute();
const tagsViewStore = useTagsViewStore();

function iframeUrl(url:string, query:any) {
  if (Object.keys(query).length > 0) {
    let params = Object.keys(query).map((key) => key + "=" + query[key]).join("&");
    return url + "?" + params;
  }
  return url;
}
</script>
