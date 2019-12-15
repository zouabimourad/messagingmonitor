import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {MainComponent} from './main/main.component';
import {AgentsComponent} from './agents/agents.component';
import {
  AgentsResolver,
  BrokersResolver,
  ConsumerDefaultCommandResolver,
  ProducerDefaultCommandResolver,
  ProtocolsResolver
} from './backend.service';
import {BrokersComponent} from './brokers/brokers.component';

const routes: Routes = [
  {
    path: '', component: MainComponent, resolve: {
      protocols: ProtocolsResolver,
      agents: AgentsResolver,
      brokers: BrokersResolver,
      producerDefaultCommand: ProducerDefaultCommandResolver,
      consumerDefaultCommand: ConsumerDefaultCommandResolver
    }
  },
  {
    path: 'agents', component: AgentsComponent,
    resolve: {
      agents: AgentsResolver
    }
  },
  {
    path: 'brokers', component: BrokersComponent,
    resolve: {
      protocols: ProtocolsResolver,
      brokers: BrokersResolver
    }
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {enableTracing: false, useHash: true})],
  exports: [RouterModule]
})
export class AppRoutingModule {


}
