/*
 * Copyright (C) 2021 Kevin Zatloukal.  All rights reserved.  Permission is
 * hereby granted to students registered for University of Washington
 * CSE 331 for use solely during Spring Quarter 2021 for purposes of
 * the course.  No other use, copying, distribution, or modification
 * is permitted without prior written consent. Copyrights for
 * third-party components of this work must be honored.  Instructors
 * interested in reusing these course materials should contact the
 * author.
 */

import React, {Component} from 'react';

import "./MapView.css";
import {Edge} from "./App";

interface MapViewState {
    backgroundImage: HTMLImageElement | null;
}

interface MapViewProps {
    edgeList: Edge[]; // list of edges that make up a path
    start: [number, number]; // coordinates of starting building
    dest: [number, number]; // coordinates of ending building
}

class MapView extends Component<MapViewProps, MapViewState> {

    // NOTE:
    // This component is a suggestion for you to use, if you would like to.
    // It has some skeleton code that helps set up some of the more difficult parts
    // of getting <canvas> elements to display nicely with large images.
    //
    // If you don't want to use this component, you're free to delete it.

    canvas: React.RefObject<HTMLCanvasElement>;

    constructor(props: MapViewProps) {
        super(props);
        this.state = {
            backgroundImage: null
        };
        this.canvas = React.createRef();
    }

    componentDidMount() {
        this.fetchAndSaveImage();
        this.redraw();
    }

    componentDidUpdate() {
        this.redraw();
    }

    fetchAndSaveImage() {
        // Creates an Image object, and sets a callback function
        // for when the image is done loading (it might take a while).
        let background: HTMLImageElement = new Image();
        background.onload = () => {
            this.setState({
                backgroundImage: background
            });
        };
        // Once our callback is set up, we tell the image what file it should
        // load from. This also triggers the loading process.
        background.src = "./campus_map.jpg";
    }

    redraw() {
        let canvas = this.canvas.current;
        if (canvas === null) throw Error("Unable to draw, no canvas ref.");
        let ctx = canvas.getContext("2d");
        if (ctx === null) throw Error("Unable to draw, no valid graphics context.");
        this.drawBackgroundImage(canvas, ctx);

        // if there is a start building selected, show a marker
        if (this.props.start[0] >= 0 && this.props.start[1] >= 0) {
            this.drawDot(ctx, this.props.start[0], this.props.start[1], "magenta");
        }

        // if there is an end building selected, show a marker
        if (this.props.dest[0] >= 0 && this.props.dest[1] >= 0) {
            this.drawDot(ctx, this.props.dest[0], this.props.dest[1], "blue");
        }

        // for each edges that needs to be outputted, draw on canvas
        for (let edge of this.props.edgeList) {
            this.drawLine(ctx, edge);
        }
    }

    drawDot(ctx: CanvasRenderingContext2D, x:number, y:number, color:string) {
        ctx.fillStyle = color;
        const radius = 15;
        ctx.lineWidth = radius*2;
        ctx.beginPath();
        ctx.arc(x, y, radius, 0, 2 * Math.PI);
        ctx.fill();
    }

    drawLine(ctx: CanvasRenderingContext2D, edge: Edge) {
        ctx.strokeStyle = "red";
        ctx.lineWidth = 4;
        ctx.beginPath();
        ctx.moveTo(edge.start[0], edge.start[1]);
        ctx.lineTo(edge.end[0], edge.end[1]);
        ctx.stroke();
    }

    drawBackgroundImage(canvas: any, ctx: CanvasRenderingContext2D) {
        if (this.state.backgroundImage !== null) { // This means the image has been loaded.
            // Sets the internal "drawing space" of the canvas to have the correct size.
            // This helps the canvas not be blurry.
            canvas.width = this.state.backgroundImage.width;
            canvas.height = this.state.backgroundImage.height;
            ctx.drawImage(this.state.backgroundImage, 0, 0);
        }
    }

    render() {
        return (
            <canvas ref={this.canvas}/>
        )
    }
}

export default MapView;
