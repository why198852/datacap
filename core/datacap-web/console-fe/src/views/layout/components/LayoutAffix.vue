<template>
  <div>
    <Affix :offset-bottom="20" style="width: 200px; float: right;">
      <Dropdown class="datacap-affix">
        <Button type="primary" shape="circle" icon="ios-apps"></Button>
        <template #list>
          <DropdownMenu>
            <DropdownItem>
              <a target="_blank" href="https://gitee.com/EdurtIO/datacap">
                <img width="13" src='https://gitee.com/EdurtIO/datacap/widgets/widget_5.svg'>
                Gitee
              </a>
            </DropdownItem>
            <DropdownItem divided>
              <a target="_blank" href="https://gitee.com/EdurtIO/datacap/members">
                <Icon type="md-infinite"/>
                Forks <img src='https://gitee.com/EdurtIO/datacap/badge/fork.svg?theme=white' alt='fork'/>
              </a>
            </DropdownItem>
            <DropdownItem>
              <a target="_blank" href="https://gitee.com/EdurtIO/datacap/stargazers">
                <Icon type="md-star"/>
                Stars <img src='https://gitee.com/EdurtIO/datacap/badge/star.svg?theme=white' alt='star'/>
              </a>
            </DropdownItem>
            <DropdownItem divided>
              <a target="_blank" href="https://github.com/EdurtIO/datacap">
                <Icon type="logo-github"/>
                GitHub
              </a>
            </DropdownItem>
            <DropdownItem divided>
              <a target="_blank" href="https://github.com/EdurtIO/datacap/fork">
                <Icon type="md-infinite"/>
                Forks <img alt="GitHub stars" src="https://img.shields.io/github/forks/EdurtIO/datacap?logo=github">
              </a>
            </DropdownItem>
            <DropdownItem>
              <a target="_blank" href="https://github.com/EdurtIO/datacap/stargazers">
                <Icon type="md-star"/>
                Stars <img alt="GitHub stars" src="https://img.shields.io/github/stars/EdurtIO/datacap?logo=github">
              </a>
            </DropdownItem>
          </DropdownMenu>
        </template>
      </Dropdown>
    </Affix>
  </div>
</template>

<script lang="ts">
export default {
  name: "LayoutAffix"
};
</script>
<style scoped>
.datacap-affix {
  background-color: transparent;
  float: right;
  margin-right: 30px;
}
</style>
