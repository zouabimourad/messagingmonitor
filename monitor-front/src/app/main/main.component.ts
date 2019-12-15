import {Component, OnInit, QueryList, ViewChildren} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {TaskComponent} from '../task/task.component';
import {BackendService} from '../backend.service';
import {noop} from 'rxjs';

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.css']
})
export class MainComponent implements OnInit {

  private agents: any = [];
  private brokers: any = [];
  private runningTasks = [];
  private runningTasksCache = {};
  private protocols = [];
  private producerDefaultCommand: any = {};
  private consumerDefaultCommand: any = {};

  @ViewChildren(TaskComponent) tasksComponents: QueryList<TaskComponent>;

  constructor(private route: ActivatedRoute, private backendService: BackendService) {

    this.route.data.subscribe(data => {
      this.protocols = data.protocols;
      this.agents = data.agents;
      this.brokers = data.brokers;
      this.producerDefaultCommand = data.producerDefaultCommand;
      this.consumerDefaultCommand = data.consumerDefaultCommand;
    });

  }

  ngOnInit() {

    const url = new URL('', window.location.href);
    url.protocol = url.protocol.replace('http', 'ws');

    const reportWebSocket = new WebSocket(url.href + 'api/report');
    reportWebSocket.onmessage = (message) => {
      const report = JSON.parse(message.data);

      let taskComponent: TaskComponent;
      taskComponent = this.runningTasksCache[report.requestId];

      if (taskComponent == null) {
        taskComponent = this.tasksComponents.find(t => t.getTaskSummary().requestId === report.requestId);
        if (taskComponent != null) {
          this.runningTasksCache[report.requestId] = taskComponent;
        }
      }

      if (taskComponent != null) {
        taskComponent.onNewReport(report);
      }
    };

    const runningTasksWebSocket = new WebSocket(url.href + 'api/runningTasks');
    runningTasksWebSocket.onopen = () => {
      this.backendService.getRunningTasks().subscribe(noop);
    };

    runningTasksWebSocket.onmessage = (message) => {
      const runningTasks = JSON.parse(message.data);
      this.onNewTasks(runningTasks);
    };

  }

  onNewTasks(taskSummaryList: any) {
    this.runningTasks.unshift(...taskSummaryList);
    this.runningTasks = [...this.runningTasks];
  }

  cancelRunningTasks() {
    this.backendService.cancelRunningTasks().subscribe(noop);
  }

  anyRunningTask() {
    return this.runningTasks.find(runningTask => runningTask.lastReport !== null && runningTask.lastReport.status === 'RUNNING') != null;
  }
}
