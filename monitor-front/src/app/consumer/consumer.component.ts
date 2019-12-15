import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {BackendService} from '../backend.service';
import {NzNotificationService} from 'ng-zorro-antd';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {merge} from 'rxjs';

@Component({
  selector: 'app-consumer',
  templateUrl: './consumer.component.html',
  styleUrls: ['./consumer.component.css']
})
export class ConsumerComponent implements OnInit {

  consumerForm: FormGroup;

  @Input()
  agents: any;

  @Input()
  brokers: any;

  @Input()
  protocols: any;

  @Input()
  defaultCommand: any;

  @Output() task: EventEmitter<any> = new EventEmitter();

  loading = false;

  constructor(private backendService: BackendService, private notification: NzNotificationService, private fb: FormBuilder) {

  }

  ngOnInit() {

    this.consumerForm = this.fb.group({

      agentPublicId: [null, Validators.required],
      protocol: [this.defaultCommand.protocol, Validators.required],

      brokerPublicId: [this.defaultCommand.brokerPublicId, Validators.required],

      clientId: [null],

      destination: [this.defaultCommand.destination, Validators.required],

      mqttQos: [this.defaultCommand.mqttQos, Validators.required],

      jmsDestinationType: [this.defaultCommand.jmsDestinationType, Validators.required],

      jmsDurableConsumer: [false, Validators.required],
      jmsConsumerName: [null],

      jmsSharedConsumer: [null],

      messagesCount: [this.defaultCommand.messagesCount, Validators.required],
      consumptionPeriod: [this.defaultCommand.consumptionPeriod, Validators.required],

      commandsCount: [1,  Validators.required]

    });

    const controls = this.consumerForm.controls;

    const jmsDurableConsumerNameValidationListener = (v) => {
      const jmsConsumerName = controls.jmsConsumerName;

      if ((controls.jmsDurableConsumer.value || controls.jmsSharedConsumer.value)
        && controls.protocol.value === 'AMQP'
        && controls.jmsDestinationType.value === 'TOPIC') {

        jmsConsumerName.setValidators([Validators.required , Validators.minLength(3)]);
      } else {
        jmsConsumerName.clearValidators();
      }

      jmsConsumerName.updateValueAndValidity();
    };

    merge(controls.jmsDurableConsumer.valueChanges, controls.jmsSharedConsumer.valueChanges, controls.protocol.valueChanges,
      controls.jmsDestinationType.valueChanges).subscribe(jmsDurableConsumerNameValidationListener);

  }

  command() {
    this.loading = true;
    this.backendService.postConsumerCommand(this.consumerForm.value)
      .subscribe(taskSummary => this.task.emit(taskSummary),
        (error) => {
          this.notification.error('Consumer creation error', 'Agent may be down or has no workers available');
          this.loading = false;
        }, () => this.loading = false);
  }

}
