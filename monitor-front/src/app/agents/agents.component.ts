import {Component, OnInit} from '@angular/core';
import {BackendService} from '../backend.service';
import {ActivatedRoute} from '@angular/router';
import {FormBuilder} from '@angular/forms';

@Component({
  selector: 'app-agents',
  templateUrl: './agents.component.html',
  styleUrls: ['./agents.component.css']
})
export class AgentsComponent implements OnInit {

  agents: any;

  constructor(private backendService: BackendService, private route: ActivatedRoute, private fb: FormBuilder) {
    this.route.data.subscribe(data => this.agents = data.agents);
  }

  ngOnInit() {

  }

}
