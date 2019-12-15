import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {HttpClientModule} from '@angular/common/http';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {MainComponent} from './main/main.component';
import {
  AgentsResolver,
  BackendService,
  BrokersResolver,
  ConsumerDefaultCommandResolver,
  ProducerDefaultCommandResolver,
  ProtocolsResolver,
  RunningTaksResolver
} from './backend.service';
import {PeerFormComponent} from './peer-form/peer-form.component';
import {ProducerComponent} from './producer/producer.component';
import {ConsumerComponent} from './consumer/consumer.component';
import {en_US, NgZorroAntdModule, NZ_I18N} from 'ng-zorro-antd';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {registerLocaleData} from '@angular/common';
import en from '@angular/common/locales/en';
import {AgentsComponent} from './agents/agents.component';
import {TaskComponent} from './task/task.component';
import {NgxChartsModule} from '@swimlane/ngx-charts';
import {BrokersComponent} from './brokers/brokers.component';

registerLocaleData(en);

@NgModule({
  declarations: [
    AppComponent,
    MainComponent,
    PeerFormComponent,
    ProducerComponent,
    ConsumerComponent,
    AgentsComponent,
    BrokersComponent,
    TaskComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    AppRoutingModule,
    NgZorroAntdModule,
    NgxChartsModule,
    BrowserAnimationsModule
  ],
  providers: [
    BackendService,
    ProtocolsResolver, AgentsResolver, BrokersResolver, ProducerDefaultCommandResolver, ConsumerDefaultCommandResolver, RunningTaksResolver,
    {
      provide: NZ_I18N,
      useValue: en_US
    }],
  bootstrap: [AppComponent]
})
export class AppModule {


}
