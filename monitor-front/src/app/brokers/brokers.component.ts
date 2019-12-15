import {Component, OnInit} from '@angular/core';
import {BackendService} from '../backend.service';
import {mergeMap} from 'rxjs/operators';
import {ActivatedRoute} from '@angular/router';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';

@Component({
  selector: 'app-brokers',
  templateUrl: './brokers.component.html',
  styleUrls: ['./brokers.component.css']
})
export class BrokersComponent implements OnInit {

  brokers: any[] = [];

  protocols: any;

  brokerForm: FormGroup;

  constructor(private backendService: BackendService, private route: ActivatedRoute, private fb: FormBuilder) {

    this.route.data.subscribe(data => {
      this.brokers = data.brokers;
      this.protocols = data.protocols;
    });

  }

  ngOnInit() {

    this.brokerForm = this.fb.group({

      host: [null, Validators.required],
      secured: [null],

      username: [null],
      password: [null],

      supportsMqtt: [true],
      mqttPort: [this.protocols.find(p => p.code === 'MQTT').defaultPort, Validators.required],

      supportsAmqp: [true],
      amqpPort: [this.protocols.find(p => p.code === 'AMQP').defaultPort, Validators.required],

    });

    const controls = this.brokerForm.controls;

    controls.supportsMqtt.valueChanges.subscribe(value => {
      if (value) {
        controls.mqttPort.setValidators([Validators.required]);
      } else {
        controls.mqttPort.clearValidators();
      }
      controls.mqttPort.updateValueAndValidity();
    });

    controls.supportsAmqp.valueChanges.subscribe(value => {
      if (value) {
        controls.amqpPort.setValidators([Validators.required]);
      } else {
        controls.amqpPort.clearValidators();
      }
      controls.amqpPort.updateValueAndValidity();
    });

  }

  save() {
    const controls = this.brokerForm.controls;
    this.backendService.saveBroker(this.brokerForm.value)
      .pipe(
        mergeMap(_ => this.backendService.getBrokers())
      ).subscribe(brokers => this.brokers = brokers);
  }

  delete(broker: any) {
    this.backendService.deleteBroker(broker.publicId)
      .pipe(
        mergeMap(_ => this.backendService.getBrokers())
      ).subscribe(brokers => this.brokers = brokers);
  }

}
