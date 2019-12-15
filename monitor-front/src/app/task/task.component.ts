import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'tr[app-task]',
  templateUrl: './task.component.html',
  styleUrls: ['./task.component.css']
})
export class TaskComponent implements OnInit {

  @Input()
  taskSummary: any;

  @Input()
  agents: any;

  multi: any[] = [
    {
      name: 'Rate',
      series: []
    }
  ];

  view: any[] = [500, 160];

  colorScheme = {
    domain: []
  };

  status;

  constructor() {

  }

  ngOnInit() {
    if (this.taskSummary.command.type === 'producer') {
      this.colorScheme.domain.push('#5AA454');
    } else if (this.taskSummary.command.type === 'consumer') {
      this.colorScheme.domain.push('#2423a4');
    }


    this.status = this.taskSummary.status;
  }

  public onNewReport(report: any) {
    const date = new Date();
    this.multi[0].series.push({
      name: date.getHours() + ':' + date.getMinutes() + ':' + date.getSeconds(),
      value: report.rate
    });

    this.taskSummary.lastReport = report;
    this.multi = [...this.multi];
  }

  isAMQPTopicConsumer(): boolean {
    const command = this.taskSummary.command;
    return command.type === 'consumer' && command.protocol === 'AMQP' && command.jmsDestinationType === 'TOPIC';
  }

  getAgentName(publicId: string): string {
    const agent = this.agents.find(it => it.publicId === publicId);
    if (agent != null) {
      return agent.name;
    }
    return '';
  }

  public getTaskSummary(): any {
    return this.taskSummary;
  }

}
