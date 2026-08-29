<!-- Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ -->
<script setup>
import {onMounted,reactive,ref} from 'vue'
import {request} from '../api'
const plans=ref([]),tasks=ref([]),summary=ref({}),error=ref('')
const tomorrow=()=>new Date(Date.now()+86400000).toISOString().slice(0,19)
const form=reactive({planNo:'PLAN-'+Date.now(),planName:'生产设备日检',routeName:'生产车间一线',frequency:'DAILY',owner:'设备部',pointCodes:['P-01','P-02','P-03']})
const hash='a'.repeat(64)
async function load(){[plans.value,tasks.value,summary.value]=await Promise.all([request('/api/core/inspection/plans'),request('/api/core/inspection/tasks'),request('/api/core/inspection/summary')])}
async function run(fn){try{error.value='';await fn();await load()}catch(e){error.value=e.message}}
async function createPlan(){await run(async()=>{await request('/api/core/inspection/plans',{method:'POST',body:JSON.stringify(form)});form.planNo='PLAN-'+Date.now()})}
async function planAction(plan,action){await run(()=>action==='publish'?request(`/api/admin/core/inspection/plans/${plan.id}/publish`,{method:'POST'}):request(`/api/core/inspection/plans/${plan.id}/tasks`,{method:'POST',body:JSON.stringify({taskNo:'INS-'+Date.now(),assignee:'巡检员',dueAt:tomorrow()})}))}
async function taskAction(t,a){await run(async()=>{let path=`/api/core/inspection/tasks/${t.id}/${a}`,body;if(a==='point'){path=`/api/core/inspection/tasks/${t.id}/points`;body={pointCode:'P-'+String(t.completedPoints+1).padStart(2,'0'),passed:true,reading:'检查正常',evidenceHash:hash}}if(a==='close')path=`/api/admin/core/inspection/tasks/${t.id}/close`;await request(path,{method:'POST',body:body?JSON.stringify(body):undefined})})}
onMounted(load)
</script>
<template>
  <section class="head"><div><span>RISK-BASED INSPECTION</span><h3>巡检计划与任务调度台</h3><p>先定义路线与受控点位，再发布计划、生成任务和采集证据，避免现场任务随意增删检查项。</p></div><div class="metric"><b>{{plans.filter(p=>p.status==='PUBLISHED').length}}</b><small>已发布计划</small><b>{{summary.openHazards||0}}</b><small>未销项隐患</small></div></section>
  <p class="err" v-if="error">{{error}}</p>
  <form class="create" @submit.prevent="createPlan"><input v-model="form.planNo"><input v-model="form.planName"><input v-model="form.routeName"><select v-model="form.frequency"><option>DAILY</option><option>WEEKLY</option><option>MONTHLY</option><option>ON_DEMAND</option></select><button>建立巡检计划</button></form>
  <section class="plans"><h4>计划版本库</h4><article v-for="p in plans" :key="p.id"><div><code>{{p.planNo}}</code><b>{{p.planName}}</b><small>{{p.routeName}} · {{p.frequency}} · {{p.pointCodes.join(' / ')}}</small></div><span>{{p.status}}</span><div><button v-if="p.status==='DRAFT'" @click="planAction(p,'publish')">发布计划</button><button v-if="p.status==='PUBLISHED'" @click="planAction(p,'generate')">生成任务</button></div></article></section>
  <section class="cards"><h4>执行任务</h4><article v-for="t in tasks" :key="t.id"><div><code>{{t.taskNo}}</code><h4>{{t.routeName}}</h4><p>{{t.assignee}} · {{t.completedPoints}} / {{t.plannedPoints}} 个点位 · 异常 {{t.failedPoints}}</p></div><b>{{t.status}}</b><div class="progress"><i :style="{width:(t.completedPoints/t.plannedPoints*100)+'%'}"></i></div><div><button v-if="t.status==='PLANNED'" @click="taskAction(t,'start')">开始巡检</button><button v-if="t.status==='IN_PROGRESS'&&t.completedPoints<t.plannedPoints" @click="taskAction(t,'point')">提交受控点位</button><button v-if="t.status==='IN_PROGRESS'&&t.completedPoints===t.plannedPoints" @click="taskAction(t,'close')">关闭任务</button></div></article></section>
</template>
<style scoped>
.head,.create,.plans,.cards{background:#fff;border:1px solid #dce2df;margin-top:20px;padding:24px}.head{display:flex;justify-content:space-between}.head span{font-size:11px;letter-spacing:.15em;color:#a56c24}.head h3{margin:6px 0}.head p{margin:0;color:#68777c}.metric{display:grid;grid-template-columns:auto auto;gap:3px 12px}.metric b{font-size:24px}.create{display:grid;grid-template-columns:1fr 1.5fr 1.5fr 1fr auto;gap:10px}.create input,.create select{padding:10px;border:1px solid #ccd6d2}.create button,.plans button,.cards button{border:0;background:#235a74;color:white;padding:8px 12px}.plans article{display:grid;grid-template-columns:1fr 120px auto;align-items:center;gap:15px;padding:13px 0;border-top:1px solid #edf0ef}.plans b,.plans small{display:block;margin-top:4px}.plans small{color:#68777c}.cards>article{display:grid;grid-template-columns:1fr 120px 180px auto;gap:15px;align-items:center;padding:15px 0;border-top:1px solid #edf0ef}.cards h4{margin:4px 0}.cards p{margin:0;color:#68777c}.progress{height:6px;background:#e7ecea}.progress i{display:block;height:100%;background:#2f7d61}.err{color:#a03f38}@media(max-width:900px){.create,.plans article,.cards>article{grid-template-columns:1fr}.metric{display:none}}
</style>
