import React, { useState, useEffect } from "react";
import '../App.css';
import axios from 'axios';
import { onlyNumbers } from "../utils/utilMethods";
import { defaultFormDonacion, defaultGateway } from "../utils/utilConstants"
import TextField from "@mui/material/TextField";
import Typography from "@mui/material/Typography";
import Stack from "@mui/material/Stack";
import FormHelperText from "@mui/material/FormHelperText";
import InputAdornment from "@mui/material/InputAdornment";
import FormControl from "@mui/material/FormControl";
import FormControlLabel from '@mui/material/FormControlLabel';
import Grid from "@mui/material/Grid";
import Button from "@mui/material/Button";
import MenuItem from '@mui/material/MenuItem';
import Select from '@mui/material/Select';
import Checkbox from '@mui/material/Checkbox';
import TaskAltRoundedIcon from '@mui/icons-material/TaskAltRounded';
import DonacionEnFilialService from "../services/DonacionEnFilialService";

function DonacionEnFilialPage() {
    const [idCategoriaOtros, setIdCategoriaOtros] = useState(-1);
    const [categorias, setCategorias] = useState([]);

    useEffect(() => {
        fetchCategorias();
    }, []);

    const [boolCategoriaDescripcion, setBoolCategoriaDescripcion] = useState(true);
    const [form, setForm] = useState(defaultFormDonacion);
    const [btnDisabled, setBtnDisabled] = useState(true);

    const fetchCategorias = async () => {
        try {
            const response = await axios.get(defaultGateway + '/categoria/all');
            const data = response.data;
            setCategorias(data);
            let laCategoria = data.find((element) => element.nombre === "Otros");
            setIdCategoriaOtros(laCategoria.id);
        } catch (error) {
            alert("Error obteniendo categorías: " + error);
        }
    }

    const verificaIngresoDatos = (tmpForm) => {
        if (tmpForm.dni_donante.trim() !== "" && tmpForm.nombre_completo_donante.toString().trim() !== "" && tmpForm.id_categoria !== 0 &&
            tmpForm.descripcion_donacion.trim() !== "" && tmpForm.cantidad.trim() !== "") {
            setBtnDisabled(false)
        } else {
            setBtnDisabled(true)
        };
    }

    const handleChange = (e) => {
        let tempForm = {...form};
        switch (e.target.id) {
            case "dni_donante": tempForm = {...tempForm, dni_donante: e.target.value}; break;
            case "nombre_completo_donante": tempForm = {...tempForm, nombre_completo_donante: e.target.value}; break;
            case "descripcion_donacion": tempForm = {...tempForm, descripcion_donacion: e.target.value}; break;
            case "cantidad": tempForm = {...tempForm, cantidad: e.target.value}; break;
            default: break;
        }
        if (e.target.name === "id_categoria") {
            tempForm = {...tempForm, id_categoria: e.target.value};
        }
        setForm(tempForm);
        verificaIngresoDatos(tempForm);
    }

    const handleChangeDinero = (e) => {
        let tempForm = {...form};
        if (e.target.checked) {
            setBoolCategoriaDescripcion(false);
            tempForm = {...tempForm, id_categoria: idCategoriaOtros, descripcion_donacion: 'Dinero en efectivo', es_dinero: true};
        } else {
            setBoolCategoriaDescripcion(true);
            tempForm = {...tempForm, id_categoria: 0, descripcion_donacion: '', es_dinero: false};
        }
        setForm(tempForm);
        verificaIngresoDatos(tempForm);
    }

    const registrarDonacion = async () => {
        DonacionEnFilialService.registrarDonacionEnFilial(form)
            .then((response) => {
                alert(response.data);
                let ret = "/home";
                let href = window.location.href;
                href = href.substring(0, href.lastIndexOf('/'));
                window.location.replace(href+ret);
            })
            .catch((err) => {
                alert(err.response.data);
            })
    }

    return (
        <React.Fragment>
            <Grid container spacing={2} className="FullWidthPage">
                <Grid item xs={12}>
                    <Typography variant="subtitle1">Ingresar datos de la donación</Typography>
                </Grid>
                <Grid item xs={1}/>
                <Grid item container spacing={2} xs={10}>
                    <FormControl>
                        <TextField onChange={(event)=> {handleChange(event)}} value={form.dni_donante}
                                    type="text" variant="outlined" id="dni_donante" 
                                    inputProps={{
                                        maxlength: "8",
                                    }}
                                    onInput={(event) => onlyNumbers(event) }
                        />
                        <FormHelperText id="dni-donante-text">Ingrese n° de documento sin puntos</FormHelperText>
                    </FormControl>
                    <FormControl>
                        <TextField onChange={(event)=> {handleChange(event)}} value={form.nombre_completo_donante}
                                    type="text" variant="outlined" id="nombre_completo_donante" 
                                    inputProps={{
                                        maxlength: "100",
                                    }}
                        />
                        <FormHelperText id="nombre-completo-donante-text">Nombre y apellido</FormHelperText>
                    </FormControl>
                    <FormControl>
                        <FormControlLabel
                            control={
                                <Checkbox onChange={(event)=> {handleChangeDinero(event)}} checked={form.es_dinero}
                                            variant="outlined" id="es_dinero" />
                            } label="¿Entrega dinero?"
                        />
                    </FormControl>
                    <FormControl>
                        <Select onChange={(event)=> {handleChange(event)}}  disabled={!boolCategoriaDescripcion}
                                    value={form.id_categoria} name="id_categoria" variant="outlined"
                        >
                            <MenuItem value={0} disabled>
                                Seleccione una categoría
                            </MenuItem>
                            { categorias.map((categoria) => (
                                <MenuItem key={categoria.id} value={categoria.id}>{categoria.nombre}</MenuItem>
                            ))}
                        </Select>
                        <FormHelperText id="categoria-text">Seleccione la categoría del producto</FormHelperText>
                    </FormControl>
                    <FormControl>
                        <TextField onChange={(event)=> {handleChange(event)}} value={form.descripcion_donacion}
                                    type="text" variant="outlined" id="descripcion_donacion" 
                                    inputProps={{
                                        maxlength: "100",
                                    }} disabled={!boolCategoriaDescripcion}
                        />
                        <FormHelperText id="descripcion-donacion-text">Descripción del producto</FormHelperText>
                    </FormControl>
                    <FormControl>
                        <TextField onChange={(event)=> {handleChange(event)}} value={form.cantidad}
                                    type="text" variant="outlined" id="cantidad" 
                                    inputProps={{
                                        maxlength: "9",
                                    }}
                                    InputProps={{
                                        startAdornment: <InputAdornment position="start">{boolCategoriaDescripcion ? '' : '$'}</InputAdornment>,
                                    }}
                                    onInput={(event) => onlyNumbers(event) }
                        />
                        <FormHelperText id="cantidad-text">{boolCategoriaDescripcion ? 'Cantidad' : 'Monto'}</FormHelperText>
                    </FormControl>
                </Grid>
                <Grid item xs={1}/>
                <Grid item container spacing={2} xs={10}>
                    <Stack spacing={2} direction="row">
                        <Button variant="contained" color="success" startIcon={<TaskAltRoundedIcon color="primary"/>}
                                    onClick={registrarDonacion} disabled={btnDisabled}>
                            <Typography variant="button">Guardar</Typography>
                        </Button>
                    </Stack>
                </Grid>
            </Grid>
        </React.Fragment>
    )
}

export default DonacionEnFilialPage;
