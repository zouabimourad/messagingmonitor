import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {BackendService} from '../backend.service';
import {NzNotificationService} from 'ng-zorro-antd';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';

@Component({
  selector: 'app-producer',
  templateUrl: './producer.component.html',
  styleUrls: ['./producer.component.css']
})
export class ProducerComponent implements OnInit {

  producerForm: FormGroup;

  @Input()
  agents: any;

  @Input()
  brokers: any;

  @Input() protocols: any;

  @Input()
  defaultCommand: any;

  @Output()
  task: EventEmitter<any> = new EventEmitter();

  loading = false;

  constructor(private backendService: BackendService, private notification: NzNotificationService, private fb: FormBuilder) {

  }

  ngOnInit() {

    this.producerForm = this.fb.group({

      agentPublicId: [null, Validators.required],
      protocol: [this.defaultCommand.protocol, Validators.required],

      brokerPublicId: [this.defaultCommand.brokerPublicId, Validators.required],

      clientId: [null],

      destination: [this.defaultCommand.destination, Validators.required],

      mqttQos: [this.defaultCommand.mqttQos, Validators.required],
      mqttMaxInFlight: [this.defaultCommand.mqttMaxInFlight],

      jmsDestinationType: [this.defaultCommand.jmsDestinationType, Validators.required],
      jmsDeliveryMode: [this.defaultCommand.jmsDeliveryMode],
      jmsTimeToLive: [null],

      customMessage: [false],
      messageSize: [1],
      message: ['This is a test message', Validators.required],

      messagesCount: [this.defaultCommand.messagesCount, Validators.required],

      async: [this.defaultCommand.async],
      commandsCount: [1, Validators.required]

    });

    const controls = this.producerForm.controls;
    controls.customMessage.valueChanges.subscribe(value => {
      if (value) {
        controls.message.setValidators([Validators.required]);
        controls.messageSize.clearValidators();
      } else {
        controls.message.clearValidators();
        controls.messageSize.setValidators([Validators.required]);
      }
      controls.message.updateValueAndValidity();
      controls.messageSize.updateValueAndValidity();
    });

    controls.protocol.valueChanges.subscribe(value => {
      if (value === 'MQTT') {
        controls.mqttMaxInFlight.setValidators([Validators.required]);
      } else {
        controls.mqttMaxInFlight.clearValidators();
      }
      controls.mqttMaxInFlight.updateValueAndValidity();
    });

  }

  command() {
    this.loading = true;

    this.backendService.postProducerCommand(this.producerForm.value)
      .subscribe(taskSummary => this.task.emit(taskSummary),
        (error) => {
          this.notification.error('Producer creation error', 'Agent may be down or has no workers available');
          this.loading = false;
        }, () => this.loading = false);

  }

}
