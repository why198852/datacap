<template>
  <div>
    <Modal v-model="visible" :title="title" :footer="null" width="70%" :closable="false" :mask-closable="false">
      <Row :gutter="16">
        <Col :span="4"/>
        <Col :span="5">
          <Card :bordered="false" dis-hover>
            <div style="text-align:center">
              <Avatar :size="40" :style="{'background-color': !testInfo.percent ? '#CCC' : testInfo.connected ? '#52c41a' : '#ff4d4f'}" icon="ios-person"/>
              <p>{{ !formState['type'] ? '_' : formState['type'] }}</p>
            </div>
          </Card>
        </Col>
        <Col :span="6">
          <Card :bordered="false" dis-hover>
            <div style="text-align:center">
              <Progress :percent="testInfo.percent" :status="testInfo.connected ? 'success' : 'wrong'">
                <span></span>
              </Progress>
            </div>
          </Card>
        </Col>
        <Col :span="5">
          <Card :bordered="false" dis-hover>
            <div style="text-align:center">
              <Avatar :size="40" :style="{'background-color': !testInfo.percent ? '#CCC' : testInfo.connected ? '#52c41a' : '#ff4d4f'}" icon="ios-cube"/>
              <p>{{ $t('common.source') }}</p>
            </div>
          </Card>
        </Col>
        <Col :span="3"/>
      </Row>
      <Form :model="formState" :label-width="80">
        <Tabs v-model="activeKey" :animated="false" @update:modelValue="handlerFilterConfigure($event)">
          <TabPane :label="$t('common.' + type)" v-for="type in pluginTabs" :name="type" v-bind:key="type" :disabled="!formState.type" icon="md-apps">
            <div v-if="type === 'source'">
              <RadioGroup v-if="plugins" v-model="formState.type" type="button" @on-change="handlerChangePlugin($event)">
                <div v-for="key in Object.keys(plugins)" v-bind:key="key">
                  <Divider orientation="left">{{ key }} ({{ plugins[key].length }})</Divider>
                  <Space wrap :size="[8, 16]">
                    <Tooltip v-for="plugin in plugins[key]" :content="plugin.description" transfer v-bind:key="plugin.name">
                      <Radio :label="plugin.name + ' ' + plugin.type">
                        <Avatar :src="'/static/images/plugin/' + plugin.name + '.png'" size="small"/>
                        <span style="margin-left: 2px;">{{ plugin.name }}</span>
                      </Radio>
                    </Tooltip>
                  </Space>
                </div>
              </RadioGroup>
            </div>
            <div v-else style="margin-top: 10px;">
              <Row>
                <Col :span="5"/>
                <Col :span="14">
                  <FormItem v-for="configure in pluginTabConfigure" :required="configure.required" v-bind:key="configure.field" :prop="configure.field">
                    <template #label>
                      <span v-if="configure.field !== 'configures'">{{ $t('common.' + configure.field) }}</span>
                    </template>
                    <Input v-if="configure.type === 'String'" type="text" v-model="configure.value"/>
                    <InputNumber v-else-if="configure.type === 'Number'" :max="configure.max" :min="configure.min" v-model="configure.value"/>
                    <Switch v-else-if="configure.type === 'Boolean'" v-model="configure.value"/>
                    <div v-else>
                      <div style="margin-top: 10px;">
                        <FormItem style="margin-bottom: 5px;">
                          <Button size="small" type="primary" shape="circle" icon="md-add" @click="handlerPlusConfigure(configure.value)"/>
                        </FormItem>
                        <FormItem v-for="(element, index) in configure.value" :key="index" style="margin-bottom: 5px;">
                          <Row :gutter="12">
                            <Col :span="10">
                              <FormItem>
                                <Input v-model="element.field">
                                  <template #prepend>
                                    <span>{{ $t('common.field') }}</span>
                                  </template>
                                </Input>
                              </FormItem>
                            </Col>
                            <Col :span="10">
                              <FormItem>
                                <Input v-model="element.value">
                                  <template #prepend>
                                    <span>{{ $t('common.value') }}</span>
                                  </template>
                                </Input>
                              </FormItem>
                            </Col>
                            <Col :span="2">
                              <Button size="small" type="error" shape="circle" icon="md-remove" @click="handlerMinusConfigure(element, configure.value)"/>
                            </Col>
                          </Row>
                        </FormItem>
                      </div>
                    </div>
                  </FormItem>
                </Col>
                <Col :span="5"/>
              </Row>
            </div>
          </TabPane>
        </Tabs>
      </Form>
      <template #footer>
        <Button key="cancel" type="error" size="small" :disabled="connectionLoading" @click="handlerCancel()">
          {{ $t('common.cancel') }}
        </Button>
        <Button key="test" type="primary" size="small" :loading="connectionLoading" @click="handlerTest()">
          {{ $t('common.test') }}
        </Button>
        <Button key="submit" type="primary" size="small" :disabled="!testInfo.connected" @click="handlerSave()">
          {{ $t('common.save') }}
        </Button>
      </template>
    </Modal>
  </div>
</template>
<script lang="ts">
import {SourceModel} from "@/model/SourceModel";
import {SourceService} from "@/services/SourceService";
import {emptySource} from "@/views/pages/admin/source/SourceGenerate";
import {defineComponent, reactive, ref} from "vue";
import {Configure} from "@/model/Configure";
import {clone} from 'lodash'
import SourceV2Service from "@/services/SourceV2Service";

interface TestInfo
{
  connected: boolean,
  percent: number
}

export default defineComponent({
  name: "SourceDetail",
  props: {
    isVisible: {
      type: Boolean,
      default: () => false
    },
    id: {
      type: Number,
      default: () => 0
    }
  },
  setup()
  {
    const layout = {
      labelCol: {span: 6},
      wrapperCol: {span: 12},
    };
    return {
      activeKey: ref('source'),
      layout
    };
  },
  components: {},
  data()
  {
    return {
      title: '',
      isUpdate: false,
      formState: {} as SourceModel,
      plugins: null,
      testInfo: {} as TestInfo,
      connectionLoading: false,
      pluginTabs: ['source'],
      pluginConfigure: null,
      pluginTabConfigure: null,
      applyPlugin: null
    }
  },
  created()
  {
    if (this.id <= 0) {
      this.title = 'Create New Source';
      this.formState = reactive(emptySource);
    }
    else {
      this.title = 'Modify Source';
      this.isUpdate = true;
    }
    this.handlerInitialize();
  },
  methods: {
    handlerInitialize()
    {
      if (this.id > 0) {
        SourceV2Service.getById(this.id)
          .then(response => {
            if (response.status) {
              this.formState = reactive(response.data);
              this.formState.type = this.formState.type + ' ' + this.formState.protocol;
              this.applyPlugin = response.data['schema'];
              this.pluginConfigure = response.data['schema']['configures'];
              // Clear
              this.pluginTabs = ['source'];
              this.pluginTabs = [...this.pluginTabs, ...Array.from(new Set(this.pluginConfigure.map(v => v.group)))];
            }
          });
      }
      new SourceService().getPlugins()
        .then(response => {
          if (response.status) {
            this.plugins = response.data;
          }
        });
    },
    handlerCancel()
    {
      this.visible = false;
    },
    handlerSave()
    {
      const temp = clone(this.formState.type).split(' ');
      const configure = {
        id: this.id,
        type: temp[1],
        name: temp[0],
        configure: this.applyPlugin
      };
      SourceV2Service.saveAndUpdate(configure, this.isUpdate)
        .then((response) => {
          if (response.status) {
            this.$Message.success("Create successful");
            this.visible = false;
          }
        });
    },
    handlerTest()
    {
      const temp = clone(this.formState.type).split(' ');
      const configure = {
        type: temp[1],
        name: temp[0],
        configure: this.applyPlugin
      };
      new SourceService()
        .testConnection(configure)
        .then((response) => {
          this.testInfo.percent = 100;
          if (response.status) {
            this.$Message.success("Test successful");
            this.testInfo.connected = true;
          }
          else {
            this.$Message.error(response.message);
            this.testInfo.connected = false;
          }
        })
        .finally(() => {
          this.connectionLoading = false;
        });
    },
    handlerPlusConfigure(array: Array<Configure>)
    {
      if (array === null) {
        array = new Array<Configure>();
      }
      const configure: Configure = {field: '', value: ''};
      array.push(configure);
    },
    handlerMinusConfigure(configure: Configure, array: Array<Configure>)
    {
      const index = array.indexOf(configure);
      if (index !== -1) {
        array.splice(index, 1);
      }
    },
    handlerChangePlugin(value)
    {
      const pluginAndType = value.split(' ');
      const applyPlugins: [] = this.plugins[pluginAndType[1]];
      const applyPlugin = applyPlugins.filter(plugin => plugin['name'] === pluginAndType[0])[0];
      this.applyPlugin = applyPlugin['configure'];
      this.pluginConfigure = applyPlugin['configure']['configures'];
      // Clear
      this.pluginTabs = ['source'];
      this.pluginTabs = [...this.pluginTabs, ...Array.from(new Set(this.pluginConfigure.map(v => v.group)))];
    },
    handlerFilterConfigure(group: string)
    {
      if (group === 'source') {
        return;
      }
      this.pluginTabConfigure = this.pluginConfigure.filter(field => field.group === group);
    }
  },
  computed: {
    visible: {
      get(): boolean
      {
        return this.isVisible
      },
      set(value: boolean)
      {
        this.$emit('close', value)
      }
    }
  }
});
</script>
